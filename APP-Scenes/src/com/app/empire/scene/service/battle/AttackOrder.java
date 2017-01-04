package com.app.empire.scene.service.battle;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.SkillActioninfo;
import com.app.db.mysql.entity.SkillBuffer;
import com.app.db.mysql.entity.SnareInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.battle.AttackBroadcastMsgProto.AttackBroadcastMsg;
import com.app.empire.protocol.pb.battle.DamageMsgProto.DamageMsg;
import com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3;
import com.app.empire.scene.constant.RoleConstants.RoleType;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.battle.action.AddDelayBuffAction;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferFactory;
import com.app.empire.scene.service.battle.buffer.BufferTargetType;
import com.app.empire.scene.service.battle.buffer.ExecWayType;
import com.app.empire.scene.service.battle.calc.SkillCalc;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.skill.Skill;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Monster;
import com.app.empire.scene.service.role.objects.Player;
import com.app.empire.scene.service.role.objects.Snare;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.util.BroadUtil;
import com.app.empire.scene.util.StringUtils;
import com.app.empire.scene.util.ThreadSafeRandom;

/**
 * <pre>
 * 攻击指令类
 * </pre>
 */
public class AttackOrder {
	protected Logger log = Logger.getLogger(AttackOrder.class);
	protected static ThreadSafeRandom rand = new ThreadSafeRandom();
	private boolean isExec;
	private Living source; // 技能源
	private Skill skill; // 使用技能
	private SkillCalc skillCalc; // 技能计算器
	private List<Living> targets; // 释放目标
	private List<Damage> damages; // 伤害
	private int orderId; // 指令伤害
	private boolean isAttack = true;
	private long attackId; // 攻击ID

	private PBVector3 current; // 施法前位置
	private PBVector3 postion; // 施法后位置

	public AttackOrder(Living source, Skill skill, long attackId) {
		this.source = source;
		this.skill = skill;
		orderId = skill.getTemplateInfo().getTemplateId();
		damages = new LinkedList<Damage>();
		this.attackId = attackId;
	}

	public AttackOrder(Living source, Skill skill, List<Living> targets, long attackId) {
		this.source = source;
		this.skill = skill;
		this.targets = targets;
		orderId = skill.getTemplateInfo().getTemplateId();
		damages = new LinkedList<Damage>();
		this.attackId = attackId;
	}

	/** 一回合的攻击 */
	public boolean exec() {
		// 判断是否能释放执行
		if (!canAction()) {
			return true;
		}
		if (targets == null || targets.size() == 0) {
			// 没有目标，只广播动作
			// Log.error("attackId :" + this.attackId);
			sendDamages();
			return false;
		}
		// 如果玩家已死亡，但使用了死亡可用的技能可以继续执行
		if (source.isDie()) {
			log.error("sourcesource: " + source);
			isAttack = false;
			return false;
		}

		// 添加/执行技能Buffer
		addSkillBuffers();
		// 技能创建陷阱
		createSnare();
		// 计算技能攻击伤害值
		damages.addAll(skillCalc.calcEffect(source, targets, this));
		// 计算受伤到一定程度后，一些特殊处理，以及伤害生效
		calcDamage();
		// 冷却技能
		coolDown();
		// 执行结束，广播伤害
		sendDamages();

		return true;
	}

	/**
	 * 给伤害相关人推送伤害包
	 */
	private void sendDamages() {
		// 获取附近的人
		Set<Integer> players = source.getNears(new PlayerSelectorHelper(source));
		// 添加自己
		players.add(source.getArmyId());
		// 广播指令
		AttackBroadcastMsg.Builder attackBroMsg = AttackBroadcastMsg.newBuilder();
		attackBroMsg.setSkillId(skill.getActionId());
		attackBroMsg.setLivingId(source.getId());
		attackBroMsg.setLivingType(source.getType());
		attackBroMsg.setExecTime(System.currentTimeMillis());
		attackBroMsg.setAttackId(attackId);

		if (targets != null && targets.size() != 0) {
			for (Living target : targets) {
				attackBroMsg.addTargets(target.getId());
			}
		}

		if (current != null) {
			attackBroMsg.setCurrent(current);
		}
		if (postion != null) {
			attackBroMsg.setPosition(postion);
		}

		if (damages == null) {
			return;
		}
		for (Damage damage : damages) {
			DamageMsg.Builder dmsg = DamageMsg.newBuilder();
			damage.writeProto(dmsg);
			attackBroMsg.addDamages(dmsg);
		}
		// Log.error("----发送给客户端伤害包-------" + attackBroMsg.build());
		BroadUtil.sendBroadcastPacket(players, Protocol.MAIN_BATTLE, Protocol.BATTLE_AttackSkill, attackBroMsg.build());

	}

	/** 再次判断该技能是否能释放执行 */
	private boolean canAction() {
		if (skill == null) {
			System.err.println("技能is null");
			return false;
		}

		if (source.isPlayer()) {
			// 判断玩家，是否能够满足执行技能条件
			if (!source.paySkillExec() || !source.canAction()) {
				isAttack = false;
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查目标是否已经Dead
	 * 
	 * @return
	 */
	public boolean checkTargetsState() {
		if (targets == null) {
			return true;
		}
		for (Living living : targets) {
			if (living.isDie()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 计算目标受到的伤害 同时判断是否会触发相应的BUFFER效果
	 */
	private void calcDamage() {
		// 攻击者行动BUFFER执行
		source.execWayBuffer(this, ExecWayType.ATTACT);

		for (Damage damage : damages) {
			Living target = damage.getTarget();
			if (target != null) {
				// 受伤时
				if (damage.isHurt()) {
					target.execWayBuffer(this, ExecWayType.HURT);
				}
				// 回复时
				if (damage.isRestore()) {

				}
				target.takeDamage(damage);

			}
		}
	}

	/**
	 * 冷却技能
	 */
	public void coolDown() {
		if (source != null && skill != null) {
			source.coolDown(skill);
		}
	}

	/**
	 * 添加技能buffer
	 */
	public void addSkillBuffers() {
		SkillActioninfo skillAction = skill.getTemplateInfo();
		int random = skillAction.getRandom();
		if (rand.next(10000) >= random) {
			return;
		}
		int[] bufferIds = StringUtils.getStr2Ids(skillAction.getBufferIds());

		if (bufferIds == null || bufferIds.length == 0) {
			return;
		}

		for (int i = 0; i < bufferIds.length; i++) {
			int bufferId = bufferIds[i];
			SkillBuffer temp = ServiceManager.getManager().getGameConfigService().getSkillBufferTemps().get(bufferId);

			if (temp == null) {
				continue;
			}
			if (temp.getDelay() == 0) {
				if (temp.getTargetType() == BufferTargetType.SOURCE) {
					Buffer buff = BufferFactory.createBuffer(source, source, temp);
					source.addBuffer(buff);
				}
				if (temp.getTargetType() == BufferTargetType.SKILL_TARGET) {
					for (Living target : targets) {
						Buffer buff = BufferFactory.createBuffer(source, target, temp);
						buff.setPermanent(false);
						target.addBuffer(buff);
					}
				}
			} else {
				AddDelayBuffAction delayAction = new AddDelayBuffAction(source, targets, temp);
				source.enDelayQueue(delayAction);
			}
		}

		// 魂幡技能buffer
		if (source.getType() != RoleType.player) {
			return;
		}
		// 是否属于普攻技能（普攻技能主类型3）
		if (skill.getTemplateInfo() == null || skill.getTemplateInfo().getMasterType() != 3) {
			return;
		}
		Player player = (Player) source;
		int index = skill.getTemplateInfo().getSonType() - 1;
		Buffer bufCreater = player.getFuseBuffer(index);
		if (bufCreater != null) {
			if (bufCreater.checkValid()) {
				bufCreater.execute(null, Damage.DEFAULT, Damage.DEFAULT, ExecWayType.FUSE);
			}
		}
	}

	public int[] getBufferIds(String bufferIds) {
		int[] bufferIdArr = new int[0];
		if (bufferIds != null) {
			String[] bids = bufferIds.split(",");
			bufferIdArr = new int[bids.length];
			for (int i = 0; i < bids.length; i++) {
				if (bids[i].isEmpty()) {
					continue;
				}
				int bufferId = Integer.valueOf(bids[i]);
				if (bufferId == 0) {
					continue;
				}
				bufferIdArr[i] = bufferId;
			}
		}
		return bufferIdArr;
	}

	/**
	 * 技能创建陷阱
	 */
	private void createSnare() {
		if (source.getType() != RoleType.monster) {
			return;
		}
		SkillActioninfo skillAction = skill.getTemplateInfo();
		Monster monster = (Monster) source;
		int[] snareArr = StringUtils.getStr2Ids(skillAction.getSnareIds());
		if (snareArr == null || snareArr.length == 0) {
			return;
		}
		for (int snareId : snareArr) {
			SnareInfo stemp = ServiceManager.getManager().getGameConfigService().getSnareInfoTemps().get(snareId);
			if (stemp == null) {
				continue;
			}

			Living target = null;
			if (targets != null && targets.size() > 0) {
				target = targets.get(0);
			}
			Snare snare = new Snare(stemp, monster, target);
			monster.addSnare(snare);
			snare.setArmyId(monster.getArmyId());

			snare.setPostion(BattleUtil.getBornPos(monster, target, stemp));
			Field field = monster.getField();
			if (field != null) {
				field.enterField(snare);
			}
		}
	}

	public boolean isExec() {
		return isExec;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("source:" + source.getArmyId());
		for (Living target : targets) {
			sb.append("targets:" + target.getArmyId() + "  ");
		}
		sb.append("skill :" + skill.getSkillId());
		return sb.toString();

	}

	public Living getSource() {
		return source;
	}

	public Skill getSkill() {
		return skill;
	}

	public List<Damage> getDamages() {
		return damages;
	}

	public void setTargets(List<Living> targets) {
		this.targets = targets;
	}

	public List<Living> getTargets() {
		return targets;
	}

	public int getOrderId() {
		return orderId;
	}

	public SkillCalc getSkillCalc() {
		return skillCalc;
	}

	public void setSkillCalc(SkillCalc skillCalculator) {
		this.skillCalc = skillCalculator;
	}

	public boolean isAttack() {
		return isAttack;
	}

	public void setCurrent(PBVector3 current) {
		this.current = current;
	}

	public void setPostion(PBVector3 postion) {
		this.postion = postion;
	}

}