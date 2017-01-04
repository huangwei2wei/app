package com.app.empire.scene.service.role.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.db.mysql.entity.SnareInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.battle.DamageListMsgProtocol.DamageListMsg;
import com.app.empire.protocol.pb.battle.DamageMsgProto.DamageMsg;
import com.app.empire.protocol.pb.battle.SnareTargetsChangeMsgProto.SnareTargetsChangeMsg;
import com.app.empire.protocol.pb.player.PlayerAttSnapProto.PlayerAttSnapMsg;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.constant.RoleConstants.RoleType;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.ai.proxy.SnareAI;
import com.app.empire.scene.service.ai.proxy.UpdatePosition;
import com.app.empire.scene.service.battle.OrderFactory;
import com.app.empire.scene.service.battle.action.AddDelayBuffAction;
import com.app.empire.scene.service.battle.action.SnarePollingAction;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferFactory;
import com.app.empire.scene.service.battle.buffer.BufferTargetType;
import com.app.empire.scene.service.battle.damage.BloodDamageCalculator;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.damage.SoulDamageCalculator;
import com.app.empire.scene.service.battle.damage.effect.DamageEffecterType;
import com.app.empire.scene.service.battle.snare.SnareConstant.ExeWay;
import com.app.empire.scene.service.battle.snare.SnareConstant.LockType;
import com.app.empire.scene.service.battle.snare.SnareConstant.TargetType;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.warField.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.StringUtils;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;

public class Snare extends ActiveLiving {
	private long createTime; // 创建时间
	private volatile int exeCount; // 执行次数
	private Set<Living> inRange; // 陷阱范围内人员
	private Set<Integer> affected; // 影响过得人员
	private Set<Living> affectingState; // 当前正作用状态的人员
	SnareInfo snareInfo;
	private ActiveLiving creater;
	private long exeTime = 0;
	private Object lock = new Object();
	private Living target;
	private Living locking;

	public static final int ACTION_EXE = 101; // 陷阱执行

	public static final int STYPE_COMMON = 1; // 普通陷阱
	public static final int STYPE_AVE = 2; // 平摊伤害

	public Snare(SnareInfo snareInfo, ActiveLiving creater, Living target) {
		super(IDMakerHelper.nextID());
		setType(RoleType.snare);
		setSkin(snareInfo.getId());
		this.snareInfo = snareInfo;
		this.createTime = System.currentTimeMillis();
		inRange = new HashSet<>();
		affected = new HashSet<>();
		affectingState = new HashSet<>();
		curSoul = snareInfo.getHp();
		this.creater = creater;
		this.armyId = creater.getArmyId();
		this.setSpeed(snareInfo.getMoveSpeed() * 100);
		setSoulState(true);

		if (creater.getType() == RoleType.monster && snareInfo.getLockingType() == LockType.FIRST_TARGET) {
			locking = target;
		}
		this.target = target;

		enDelayQueue(new SnarePollingAction(this, new SnareAI(this), new UpdatePosition(this, new PlayerSelectorHelper(this))));
	}

	// 陷阱执行 -- 对范围内玩家生效
	public void exe() {

		if (expired()) {
			onDie(this);
		}

		if (isDie()) {
			return;
		}
		if (snareInfo.getExeWay() != ExeWay.TOUCH_PRE_TIME) {
			return;
		}
		if (System.currentTimeMillis() < exeTime + snareInfo.getCoolDown() * 1000) {
			return;
		}
		// 通知执行
		notifyAction(ACTION_EXE);
		if (inRange == null || inRange.size() == 0) {
			return;
		}
		synchronized (inRange) {
			for (Living living : inRange) {
				if (!canExe(creater, living)) {
					continue;
				}
				exe(living);
				// 计数
			}
		}
		counter();
		exeTime = System.currentTimeMillis();

	}

	// 陷阱执行--对某个玩家产生效果
	private void exe(Living living) {
		// 添加buffer
		int[] bufferIds = StringUtils.getStr2Ids(snareInfo.getAddBuffers());

		if (bufferIds.length > 0) {
			for (int bufferId : bufferIds) {
				SkillBuffer temp = ServiceManager.getManager().getGameConfigService().getSkillBufferTemps().get(bufferId);
				if (temp == null) {
					continue;
				}
				if (temp.getDelay() == 0) {
					if (temp.getTargetType() == BufferTargetType.SOURCE) {
						Buffer buff = BufferFactory.createBuffer(getCreater(), getCreater(), temp);
						getCreater().addBuffer(buff);
					}
					if (temp.getTargetType() == BufferTargetType.SKILL_TARGET) {
						Buffer buff = BufferFactory.createBuffer(getCreater(), living, temp);
						living.addBuffer(buff);
					}
				} else {
					AddDelayBuffAction delayAction = new AddDelayBuffAction(getCreater(), living, temp);
					getCreater().enDelayQueue(delayAction);
				}
			}
		}
		// 产生伤害
		int bloodDamage = new BloodDamageCalculator().calcDamage(creater, living, snareInfo.getBloodPercent(), snareInfo.getBloodValue());
		int soulDamage = new SoulDamageCalculator().calcDamage(creater, living, snareInfo.getSoulPercent(), snareInfo.getSoulValue());
		int humCount = inRange.size() <= 0 ? 1 : inRange.size();
		if (snareInfo.getType() == STYPE_AVE) {
			bloodDamage = bloodDamage / humCount;
			soulDamage = soulDamage / humCount;
		}
		takeDamage(living, bloodDamage, soulDamage, DamageEffecterType.COMMON, Damage.SNARE, this.getId());
		affected.add(living.getId());
		if (creater.getType() == RoleType.monster && snareInfo.getLockingType() == LockType.FIRST_BE_ATTACK && locking == null) {
			locking = living;
		}
	}

	// 进入陷阱范围
	public void in(Living living) {
		synchronized (lock) {
			if (isDie()) {
				return;
			}
			if (!canExe(creater, living)) {
				return;
			}
			inRange.add(living);
			if (snareInfo.getStateId() != 0) {
				living.addLivingState(snareInfo.getStateId());
				affectingState.add(living);
			}
			if (snareInfo.getExeWay() == ExeWay.IN) {
				exe(living);
				counter();
				notifyAction(ACTION_EXE);
			}
			targetChange();
		}
	}

	// 离开
	public void out(Living living) {
		synchronized (lock) {
			inRange.remove(living);
			targetChange();
			if (isDie()) {
				return;
			}
			if (!canExe(creater, living)) {
				return;
			}
			if (snareInfo.getStateId() != 0) {
				living.removeLivingState(snareInfo.getStateId());
				affectingState.remove(living);
			}
			if (snareInfo.getExeWay() == ExeWay.OUT) {
				exe(living);
				counter();
				notifyAction(ACTION_EXE);
			}
		}
	}

	// 通知周边玩家，陷阱当前动作
	public void notifyAction(int state) {
		sendChangeStatuMsg(ACTION_STATU, state);
	}

	// 周边玩家人数变化
	private void targetChange() {
		SnareTargetsChangeMsg.Builder builder = SnareTargetsChangeMsg.newBuilder();
		builder.setSnareId(this.getId());
		for (Living target : inRange) {
			builder.addLivingIds(target.getId());
		}
		Set<Integer> nears = getNears(new PlayerSelectorHelper(this));
		nears.add(armyId);
		for (Integer armyId : nears) {
			ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(armyId);
			// PBMessage message = MessageUtil.buildMessage(Protocol.U_SNARE_TARGETS_INFO, builder.build());
			if (army != null) {
				army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_SnareTargets, builder.build());
			}
		}
	}

	// 陷阱死亡
	public boolean onDie(Living killer) {
		synchronized (dieLock) {
			if (this.livingState == DIE) {
				return false;
			}
			this.livingState = DIE;
		}
		sendChangeStatuMsg(LIVING, livingState);// 死亡状态不推，客户端自己判断
		// 清理掉所有受陷阱影响玩家的状态
		if (snareInfo.getStateId() != 0) {
			for (Living living : affectingState) {
				living.removeLivingState(snareInfo.getStateId());
			}
		}
		creater.removeSnare(this);
		if (getField() != null) {
			getField().leaveField(this);
		} else {
			log.error("----------------------------------------" + this.id);
		}
		return true;
	}

	public boolean expired() {
		// 1 生存时间到了
		if (snareInfo.getLifetime() != 0 && System.currentTimeMillis() >= (createTime + snareInfo.getLifetime() * 1000)) {
			return true;
		}
		// 2 生效次数消尽了
		if (snareInfo.getValidCount() != 0 && exeCount >= snareInfo.getValidCount()) {
			return true;
		}
		// 3 没血了
		if (snareInfo.getHp() > 0 && getCurSoul() <= 0) {
			return true;
		}
		return false;
	}

	/** 判断是否死亡 */
	public boolean isDie() {
		return livingState == DIE || livingState == DISTORY;
	}

	public void counter() {
		exeCount++;
		if (expired()) {
			onDie(creater);
		}
	}

	// 角色是否可被执行
	private boolean canExe(Living source, Living target) {
		// 是否在可执行范围
		if (source.getPostion() == null || target.getPostion() == null) {
			return false;
		}
		if (source.getField() != target.getField()) {
			return false;
		}
		if (target.isDie()) {
			return false;
		}

		float dist = Vector3.distance(new Vector3(source.getPostion().x, 0, source.getPostion().z), new Vector3(target.getPostion().x, 0, target.getPostion().z));
		if (dist > snareInfo.getCheckX() + 50) {
			return false;
		}

		if (isDie()) {
			return false;
		}

		// --------------- 无差别攻击----------------
		if (snareInfo.getTarget() == TargetType.ALL_OTHER) {
			if (target.getId() == source.getId()) {
				return false;
			}
			return true;
		}

		// 敌方
		if (snareInfo.getTarget() == TargetType.ENEMY) {
			// 人怪敌对
			if (source.getType() == RoleType.player && target.getType() == RoleType.monster) {
				return true;
			}
			// 怪人敌对
			if (source.getType() == RoleType.monster && target.getType() == RoleType.player) {
				return true;
			}
			// 人人敌对
			if (source instanceof Player && target instanceof Player) {
				return OrderFactory.attackCheck(source.getField(), (Player) source, (Player) target);
			}
			return false;

		}
		// ---------------------敌方仅玩家----------------
		if (snareInfo.getTarget() == TargetType.ENEMY_PLAYER) {
			// 敌方玩家
			if (source.getType() == RoleType.monster && target.getType() == RoleType.player) {
				return true;
			}
			// 人人敌对
			if ((target instanceof Player) && (source instanceof Player)) {
				return OrderFactory.attackCheck(source.getField(), (Player) source, (Player) target);
			}
			return false;
		}
		// ----------------------敌方怪物--------------
		if (snareInfo.getTarget() == TargetType.ENEMY_MONSTER) {
			if (source.getType() == RoleType.monster || target.getType() == RoleType.player) {
				return false;
			}
			return true;
		}
		// 友方
		if (snareInfo.getTarget() == TargetType.FRIENDLY) {
			if (source.getType() == RoleType.monster && target.getType() == RoleType.monster) {
				return true;
			}
			if ((source instanceof Player) && (target instanceof Player)) {
				return !OrderFactory.attackCheck(source.getField(), (Player) source, (Player) target);
			}
			return false;
		}
		return false;
	}

	public long getCreateTime() {
		return createTime;
	}

	/**
	 * 获取场景对象的快照信息
	 * 
	 * @return
	 */
	@Override
	public PlayerAttSnapMsg.Builder getAttSnapMsg() {
		if (cacheAttSnapPacker == null)
			cacheAttSnapPacker = PlayerAttSnapMsg.newBuilder();
		cacheAttSnapPacker.setPlayerId(id);
		cacheAttSnapPacker.setType(getType());
		cacheAttSnapPacker.setSkinId(getSkin());
		cacheAttSnapPacker.setPostion(Vector3BuilderHelper.build(getPostion()));
		cacheAttSnapPacker.setTarget(Vector3BuilderHelper.build(getTargetPostion()));
		cacheAttSnapPacker.setOwnerId(getArmyId());
		return cacheAttSnapPacker;
	}

	public int takeDamage(Damage damage) {
		damage.setDamageValue(0);

		if (creater.getType() == RoleType.monster && snareInfo.getLockingType() == LockType.ATTACKER && locking == null) {
			locking = damage.getSource();
		}
		return 0;
	}

	public void takeDamage(Living target, int bloodDamageValue, int soulDamageValue, int type, int fromType, long fromId) {
		List<Damage> damages = new ArrayList<>();
		Damage blood = new Damage(target, getCreater());
		blood.setDamageType(EnumAttr.CUR_BLOOD.getValue());
		blood.setDamageValue(bloodDamageValue);
		blood.setCalcType(type);
		blood.setFromType(fromType);
		blood.setFromId(fromId);
		damages.add(blood);
		target.takeDamage(blood);

		Damage soul = new Damage(target, getCreater());
		soul.setDamageType(EnumAttr.CUR_SOUL.getValue());
		soul.setDamageValue(soulDamageValue);
		soul.setCalcType(type);
		soul.setFromType(fromType);
		soul.setFromId(fromId);
		damages.add(soul);
		target.takeDamage(soul);

		DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
		damagesPb.setAttackId(-1);
		for (Damage d : damages) {
			DamageMsg.Builder dmsg = DamageMsg.newBuilder();
			d.writeProto(dmsg);
			damagesPb.addDamages(dmsg);
		}
		Set<Integer> players = target.getNears(new PlayerSelectorHelper(this));
		// 添加自己
		players.add(target.getArmyId());
		for (Integer armyId : players) {
			ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(armyId);
			// PBMessage message = MessageUtil.buildMessage(Protocol.U_G_DAMAGE, damagesPb.build());
			if (army != null) {
				army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
			}
		}
	}

	public Living getLocking() {
		return locking;
	}

	public SnareInfo getSnareInfo() {
		return snareInfo;
	}

	public ActiveLiving getCreater() {
		return creater;
	}

	public Living getTarget() {
		return target;
	}

}
