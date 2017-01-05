package com.app.empire.scene.service.role.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.SkillInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.army.PropertyListMsgProto.PropertyListMsg;
import com.app.empire.protocol.pb.army.PropertyMsgProto.PropertyMsg;
import com.app.empire.protocol.pb.battle.BattleLivingInfoMsgProto.BattleLivingInfoMsg;
import com.app.empire.protocol.pb.battle.BufferMsgProto.BufferMsg;
import com.app.empire.protocol.pb.battle.DamageListMsgProtocol.DamageListMsg;
import com.app.empire.protocol.pb.battle.DamageMsgProto.DamageMsg;
import com.app.empire.protocol.pb.battle.LivingStateChangeMsgProto.LivingStateChangeMsg;
import com.app.empire.protocol.pb.player.PlayerAttSnapProto.PlayerAttSnapMsg;
import com.app.empire.protocol.pb.player.PlayerAttUpdateProto.PlayerAttUpdateMsg;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.constant.EnumBufferState;
import com.app.empire.scene.constant.RoleConstants;
import com.app.empire.scene.constant.RoleConstants.RoleType;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.ai.proxy.AI;
import com.app.empire.scene.service.ai.proxy.UpdatePosition;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferOverlayType;
import com.app.empire.scene.service.battle.buffer.BufferState;
import com.app.empire.scene.service.battle.buffer.BufferType;
import com.app.empire.scene.service.battle.buffer.ExecWayType;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.damage.effect.DamageEffecter;
import com.app.empire.scene.service.battle.damage.effect.DamageEffecterFactory;
import com.app.empire.scene.service.battle.damage.effect.DamageEffecterType;
import com.app.empire.scene.service.battle.skill.Skill;
import com.app.empire.scene.service.cooldown.CoolDownTypes;
import com.app.empire.scene.service.cooldown.obj.CoolDown;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.grid.Grid;
import com.app.empire.scene.service.warField.grid.GridCoord;
import com.app.empire.scene.service.warField.grid.GridItem;
import com.app.empire.scene.service.warField.helper.Selector;
import com.app.empire.scene.service.warField.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.service.world.SimplePlayerInfo;
import com.app.empire.scene.util.BroadUtil;
import com.app.empire.scene.util.MathUtils;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;
import com.app.thread.exec.AbstractActionQueue;
import com.app.thread.exec.ThreadManager;

/***
 * 需要瘦身
 * 
 * @author Administrator
 * 
 */
public class Living extends AbstractActionQueue {
	protected Logger log = Logger.getLogger(Living.class);
	public static final int ALIVE = 0; // 活着
	public static final int DIE = 1; // 死亡
	public static final int DISTORY = 2; // 移除

	/** 对象的生死 生/死 */
	static final int LIVING = 1;
	/** 对象作战状态 元魂/气血 */
	static final int FIGHT_STATU = 2;
	/** 是否闪名 */
	static final int BATTLE_MODE = 3;
	/** 陷阱行为 */
	static final int ACTION_STATU = 4;
	protected AI ai;
	protected UpdatePosition updatePosition;

	// / livingId
	protected int id;
	// 部队ID
	protected int armyId;
	// / 皮肤， npc：npcid
	protected int skin;
	// / Living类型
	protected int type;
	// / 格子索引
	protected int gridIndex;

	// / 简单用户信息
	protected SimplePlayerInfo simpleInfo;
	protected int job; // 职业
	protected int initSoul; // 初始血量
	protected int maxSoul; // 最大血量
	protected int curSoul; // 当前血量

	protected int initBlood; // 初始气血(固定)
	protected int maxBlood; // 最大气血(变动)

	/**
	 * 气血效果 气血值=0 进入元魂状态，当脱离战斗状态或者气血恢复100%时脱离元魂状态 受到所有的异常状态时间延长50%，造成的伤害提高10%，魂防-50% 回复的气血值暂时没有效果，直到脱离元魂状态 离开战斗状态20秒后开始自动回复气血值 气血值>0 免疫硬直和浮空效果 战斗状态每10秒一次的自动回复气血
	 **/
	protected int curBlood; // 当前气血(变动)

	protected int mana; // 灵力

	protected int initAttack; // 初始攻击
	protected int attack; // 攻击
	protected int initDefence; // 初始防御
	protected int defence; // 防御
	protected int initSoulAttack; // 初始魂攻
	protected int soulAttack; // 魂攻
	protected int initSoulDefence; // 初始魂防
	protected int soulDefence; // 魂防
	protected int initAccurate; // 初始命中
	protected int accurate; // 命中
	protected int initDodge; // 初始闪避
	protected int dodge; // 闪避
	protected int initCrit; // 初始暴击
	protected int crit; // 暴击

	protected int critDefence; // 抗暴
	protected int critAddtion; // 暴击伤害
	protected int critCut; // 抗暴减伤
	protected int attackAddtion; // 气血伤害增加
	protected int attackCut; // 气血伤害减免
	protected int soulAttackAddtion; // 元魂伤害增加
	protected int soulAttackCut; // 元魂伤害减免
	protected int regainSoul; // 每10秒回魂
	protected int regainBlood; // 每10秒回血
	protected int metal; // 金
	protected int wood; // 木
	protected int water; // 水
	protected int fire; // 火
	protected int earth; // 土
	protected int metalDefence; // 金抗
	protected int woodDefence; // 木抗
	protected int waterDefence; // 水抗
	protected int fireDefence; // 火抗
	protected int earthDefence; // 土抗
	protected int speed = 600; // 移动速度
	protected int pkVal; // pk
							// 值
	protected int battleMode; // 攻击模式
	// 进场保护,不可攻击
	protected boolean protection;

	// 生存状态
	protected int livingState;

	/** 战斗形态 :是否处于元魂状态 */
	protected boolean isSoulState = false;

	/** 是否处于战斗状态 */
	protected boolean fightState = false;
	/** 最后一次战斗时间 */
	protected long lastFightTm;
	/** pk 值计算时间 */
	private long pkValCalTime = 0;
	/**
	 * 位置
	 */
	protected Vector3 postion;
	/**
	 * 方向
	 */
	protected Vector3 dir = Vector3.Zero();

	/**
	 * 目标位置
	 */
	protected Vector3 targetPostion = Vector3.Invalid;

	/**
	 * 所在的场景
	 */
	protected Field field;

	/** 怪物出生节点 */
	protected SpwanNode node;
	/** 缓存在地图中，别人获取自己的时候的信息 */
	protected BattleLivingInfoMsg.Builder cachBattleInfoPacket;
	/** 缓存地图中, 本人快照信息 */
	protected PlayerAttSnapMsg.Builder cacheAttSnapPacker;
	/** 主动技能 */
	protected Map<Integer, Skill> drivingSkills;
	/** 技能map type->skill */
	protected Map<String, Skill> mapSkill;

	/** 被动技能带来的常驻buffer <execWay,<bufferId,buffer>> */
	protected Map<Integer, List<Buffer>> permanentBuffer;

	/** 存活的临时buffer <execWay,<bufferId,buffer>> */

	protected Map<Integer, List<Buffer>> workBuffers;

	/** 根据buffer类型存放工作buffer映射 */
	protected Map<Integer, List<Buffer>> typeBuffers;

	/** 所有的buffer */
	protected Map<Long, Buffer> allBuffers;

	/** 状态管理器 */
	protected Map<EnumBufferState, AtomicInteger> livingStatus;

	public HashMap<String, CoolDown> getCooldowns() {
		return cooldowns;
	}

	/** buffer回收站--待定，看是否需要 */
	// -------------------------------------------
	/** (气血&&元魂)自动恢复时间 */
	protected long restoreTime = System.currentTimeMillis();

	/** 死亡时间 */
	protected long dieTime;

	protected boolean canAction;

	private static EnumAttr[] propertys = new EnumAttr[] { EnumAttr.CUR_SOUL, EnumAttr.MAX_SOUL, EnumAttr.SOUL, EnumAttr.BLOOD, EnumAttr.MAX_BLOOD, EnumAttr.CUR_BLOOD,
			EnumAttr.ATTACK, EnumAttr.DEFENCE, EnumAttr.SOUL_ATTACK, EnumAttr.SOUL_DEFENCE, EnumAttr.ACCURATE, EnumAttr.DODGE, EnumAttr.CRIT, EnumAttr.CRIT_DEFENCE,
			EnumAttr.CRIT_ADDTION, EnumAttr.CRIT_CUT, EnumAttr.ATTACK_ADDTION, EnumAttr.ATTACK_CUT, EnumAttr.SOUL_ATTACK_ADDTION, EnumAttr.SOUL_ATTACK_CUT, EnumAttr.REGAIN_SOUL,
			EnumAttr.REGAIN_BLOOD, EnumAttr.METAL, EnumAttr.WOOD, EnumAttr.WATER, EnumAttr.FIRE, EnumAttr.EARTH, EnumAttr.METAL_DEFENCE, EnumAttr.WOOD_DEFENCE,
			EnumAttr.WATER_DEFENCE, EnumAttr.FIRE_DEFENCE, EnumAttr.EARTH_DEFENCE, EnumAttr.SPEED, EnumAttr.TEAM_ID, EnumAttr.PK_VAL, EnumAttr.BATTLE_MODE, EnumAttr.MANA };

	/** 队伍ID */
	private int teamId;

	/**
	 * 魂幡值
	 */
	private long soulExp;
	/** cd对象 */
	protected HashMap<String, CoolDown> cooldowns = new HashMap<String, CoolDown>();

	protected Object dieLock = new Object();

	public Vector3 getPostion() {
		return postion;
	}

	public void setPostion(Vector3 postion) {
		// System.out.println("id = " + id + " setPosition --- " + postion);
		this.postion = postion;
	}

	public Vector3 getDir() {
		return dir;
	}

	public void setDir(Vector3 dir) {
		this.dir = dir;
	}

	public Vector3 getTargetPostion() {
		return targetPostion;
	}

	public void setTargetPostion(Vector3 targetPostion) {
		this.targetPostion = targetPostion;
	}

	public int getGridIndex() {
		return gridIndex;
	}

	public void setGridIndex(int gridIndex) {
		this.gridIndex = gridIndex;
	}

	public Field getField() {
		return field;
	}

	public Living(int armyId, int id) {
		super(ThreadManager.actionExecutor);
		this.armyId = armyId;
		this.id = id;
		this.drivingSkills = new HashMap<>();
		this.mapSkill = new HashMap<>();
		this.permanentBuffer = new ConcurrentHashMap<>();
		this.workBuffers = new ConcurrentHashMap<>();
		this.typeBuffers = new ConcurrentHashMap<>();
		this.livingState = ALIVE;
		this.postion = Vector3.Invalid;
		this.allBuffers = new ConcurrentHashMap<>();
		initState();
	}

	public Living(int id) {
		super(ThreadManager.actionExecutor);
		this.id = id;
		this.drivingSkills = new HashMap<>();
		this.mapSkill = new HashMap<>();
		this.permanentBuffer = new ConcurrentHashMap<>();
		this.workBuffers = new ConcurrentHashMap<>();
		this.typeBuffers = new ConcurrentHashMap<>();
		this.postion = Vector3.Invalid;
		this.livingState = ALIVE;
		this.allBuffers = new ConcurrentHashMap<>();
		initState();

	}

	/** 初始化状态 */
	private void initState() {
		livingStatus = new HashMap<>();
		for (EnumBufferState state : EnumBufferState.values()) {
			AtomicInteger value = new AtomicInteger(0);
			livingStatus.put(state, value);
		}
	}

	/** 判断是否死亡 */
	public boolean isDie() {
		return curSoul <= 0 || livingState == DIE || livingState == DISTORY;
	}

	/**
	 * 技能重新CD
	 */
	public void coolDown(Skill skill) {
		skill.setLastUsed(System.currentTimeMillis());
	}

	/**
	 * 进入地图/场景
	 * 
	 * @param f
	 */
	public void enterField(Field f) {
		field = f;
		// Log.error("enterField + livingId :"+ this.id +" "+ f);
	}

	/**
	 * 离开
	 * 
	 * @param f
	 */
	public void leaveField() {
		field = null;

	}

	// living当前是否处于可行动状态
	public boolean canAction() {
		return true;
	}

	// 是否足够支付技能消耗
	public boolean paySkillExec() {
		return true;
	}

	// 是否具有该技能
	public boolean hasSkillId(int skillId) {
		return true;
	}

	// 获取技能
	public Skill getSkill(int skillId) {
		// SkillActionTemplateInfo tinfo = new SkillActionTemplateInfo();
		// tinfo.setAttackType(1);
		// tinfo.setTemplateId(100);
		// tinfo.setAttackTimes(2);
		return drivingSkills.get(skillId);
	}

	/**
	 * 添加一个常驻buffer
	 */
	public void addPermanentBuffer(Buffer buff) {
		List<Buffer> exeWayBuffers = permanentBuffer.get(buff.getExeWay());
		if (exeWayBuffers == null) {
			exeWayBuffers = new ArrayList<>();
			permanentBuffer.put(buff.getExeWay(), exeWayBuffers);
		}

		List<Buffer> typeBufList = typeBuffers.get(buff.getType());
		if (typeBuffers == null) {
			typeBufList = new ArrayList<>();
			typeBuffers.put(buff.getType(), typeBufList);
		}

		exeWayBuffers.add(buff);
	}

	/**
	 * 更新buffer
	 * 
	 * @param buffer
	 */
	public void upBuffer(Buffer buffer) {
		BufferMsg.Builder bmsg = BufferMsg.newBuilder();
		bmsg.setOption(2);// 更新
		buffer.writeProto(bmsg);
		sendBufferChange(bmsg.build());
	}

	public void addBuffer(Buffer buff) {
		// 死亡不再添加buff
		if (isDie()) {
			return;
		}

		for (Buffer older : imageBuffs()) {
			if (older.getOverlayType() != 0 && buff.getOverlayWay() != 0 && buff.getOverlayType() == older.getOverlayType() && buff.getOverlayWay() == older.getOverlayWay()) {
				overlay(older, buff);
				return;
			}
		}
		simpleAdd(buff);
	}

	/**
	 * 添加一个可执行buffer
	 */
	public void simpleAdd(Buffer buff) {

		List<Buffer> exeWayBuffers = workBuffers.get(buff.getExeWay());

		if (exeWayBuffers == null) {
			exeWayBuffers = new ArrayList<>();
			workBuffers.put(buff.getExeWay(), exeWayBuffers);
		}

		List<Buffer> typeBufList = typeBuffers.get(buff.getType());
		if (typeBufList == null) {
			typeBufList = new ArrayList<>();
			typeBuffers.put(buff.getType(), typeBufList);
		}

		exeWayBuffers.add(buff);
		typeBufList.add(buff);
		allBuffers.put(buff.getBufferId(), buff);
		BufferMsg.Builder bmsg = BufferMsg.newBuilder();
		bmsg.setOption(1);// 添加
		buff.writeProto(bmsg);
		sendBufferChange(bmsg.build());

		if (buff.getStatus() != 0) {
			addLivingState(buff.getStatus());
		}

		if (buff.getBufferInfo().getExeWay() == ExecWayType.ADD && buff.checkValid()) {
			buff.execute(null, Damage.DEFAULT, Damage.DEFAULT, ExecWayType.ADD);
		}
	}

	/**
	 * 移除一个buffer
	 */
	public boolean removeBuffer(Buffer buff) {
		boolean result = false;
		List<Buffer> exeWayBuffers = workBuffers.get(buff.getExeWay());
		allBuffers.remove(buff.getBufferId());
		if (exeWayBuffers != null) {
			result = exeWayBuffers.remove(buff);
			BufferMsg.Builder bmsg = BufferMsg.newBuilder();
			bmsg.setBufferId(buff.getBufferId());
			bmsg.setOption(4);// 4 删除
			bmsg.setSourceId(buff.getSource().getId());
			bmsg.setTargetId(buff.getTarget().getId());
			sendBufferChange(bmsg.build());
			buff.stop();
		}

		List<Buffer> typeArr = typeBuffers.get(buff.getType());
		if (typeArr != null) {
			typeArr.remove(buff);
		}

		if (buff.getStatus() != 0) {
			removeLivingState(buff.getStatus());
		}
		if (buff.getType() == BufferType.ATTR_BODY) {
			if (buff.getBufferInfo().getValueType() != 0) {
				refreshProperties(buff.getBufferInfo().getValueType());
			}
			if (buff.getBufferInfo().getValueType1() != 0) {
				refreshProperties(buff.getBufferInfo().getValueType1());
			}
		}
		return result;
	}

	protected void sendBufferChange(BufferMsg msg) {
		Set<Integer> nears = getNears(new PlayerSelectorHelper(this));
		if (this.armyId > 0) {
			nears.add(this.armyId);
		}
		BroadUtil.sendBroadcastPacket(nears, Protocol.MAIN_PLAYER, Protocol.PLAYER_BUFFER, msg);
	}

	/**
	 * 获取某种执行的所有buffer
	 */
	public List<Buffer> getExeWayBuffers(int exeWay) {
		List<Buffer> toal = new ArrayList<>();

		List<Buffer> pbuff = permanentBuffer.get(exeWay);
		if (pbuff != null) {
			toal.addAll(pbuff);
		}

		List<Buffer> wbuff = workBuffers.get(exeWay);
		if (wbuff != null) {
			toal.addAll(wbuff);
		}
		return toal;
	}

	/** 获取某种类型的buff */
	public List<Buffer> getTypeBuffers(int type) {
		List<Buffer> toal = new ArrayList<>();

		List<Buffer> typeBuff = typeBuffers.get(type);
		if (typeBuff != null) {
			toal.addAll(typeBuff);
		}
		return toal;
	}

	/**
	 * 
	 * @param pmsg
	 */
	public void readProperty(PropertyListMsg pmsg) {
		List<PropertyMsg> properties = new ArrayList<>(pmsg.getPropertysList());
		readProperty(properties);
	}

	public void updataProperty(List<PropertyMsg> properties) {
		readProperty(properties);
		Set<Integer> nears = getNears(new PlayerSelectorHelper(this));
		nears.add(getArmyId());
		BroadUtil.sendBroadcastPacket(nears, Protocol.MAIN_PLAYER, Protocol.PLAYER_PLAYERINFO, getBattlePlayerInfoMsg().build());
	}

	/**
	 * 单个人物属性更新方法
	 * 
	 * @param type
	 * @param value
	 */
	public void updateProperty(EnumAttr type, long value) {
		// 修改玩家属性
		List<PropertyMsg> properties = new ArrayList<>();
		PropertyMsg.Builder p = PropertyMsg.newBuilder();

		p.setBasePoint(value);
		p.setTotalPoint(value);
		p.setType(type.getValue());
		properties.add(p.build());
		this.readProperty(properties);

		PlayerAttUpdateMsg.Builder msg = PlayerAttUpdateMsg.newBuilder();
		msg.setPlayerId(this.getArmyId());
		msg.addAtt(p);

		Set<Integer> nears = getNears(new PlayerSelectorHelper(this));
		nears.add(getArmyId());
		BroadUtil.sendBroadcastPacket(nears, Protocol.MAIN_PLAYER, Protocol.PLAYER_PROPERTY, msg.build());

	}

	/**
	 * 更新他人人物属性
	 * 
	 * @param type
	 * @param value
	 */
	public void updateProperty(Living living, List<PropertyMsg> properties) {
		// 修改玩家属性
		living.readProperty(properties);
		PlayerAttUpdateMsg.Builder msg = PlayerAttUpdateMsg.newBuilder();
		msg.setPlayerId(living.getArmyId());
		msg.addAllAtt(properties);

		Set<Integer> nears = getNears(new PlayerSelectorHelper(this));
		nears.add(living.getArmyId());
		BroadUtil.sendBroadcastPacket(nears, Protocol.MAIN_PLAYER, Protocol.PLAYER_PROPERTY, msg.build());

	}

	public void readProperty(List<PropertyMsg> properties) {
		List<PropertyMsg> temp = new ArrayList<>(properties);
		if (temp != null && temp.size() > 0) {
			for (PropertyMsg p : temp) {
				// 设置最大气血
				if (p.getType() == EnumAttr.BLOOD.getValue()) {
					long add = p.getTotalPoint() - getProperty(EnumAttr.BLOOD.getValue());
					if (add != 0) {
						long maxBlood = getProperty(EnumAttr.MAX_BLOOD.getValue());
						long newMaxB = maxBlood + add;
						setProperty(EnumAttr.MAX_BLOOD, newMaxB);
						PropertyMsg.Builder mbMsg = PropertyMsg.newBuilder();
						mbMsg.setType(EnumAttr.MAX_BLOOD.getValue());
						mbMsg.setTotalPoint(newMaxB);
						properties.add(mbMsg.build());
						addCurBlood((int) add, DamageEffecterType.BLOOD, 0, 0);
					}
				}
				// 设置最大元魂
				if (p.getType() == EnumAttr.SOUL.getValue()) {
					long add = p.getTotalPoint() - getProperty(EnumAttr.SOUL.getValue());
					if (add != 0) {
						long maxSoul = getProperty(EnumAttr.MAX_SOUL.getValue());
						long newMaxS = maxSoul + add;
						setProperty(EnumAttr.MAX_SOUL, newMaxS);
						PropertyMsg.Builder msMsg = PropertyMsg.newBuilder();
						msMsg.setType(EnumAttr.MAX_SOUL.getValue());
						msMsg.setTotalPoint(newMaxS);
						properties.add(msMsg.build());
						addCurSoul((int) add, DamageEffecterType.SOUL, 0, 0);
					}
				}
			}
			for (PropertyMsg p : properties) {
				EnumAttr attr = EnumAttr.getEnumAttrByValue(p.getType());
				setProperty(attr, p.getTotalPoint());
			}
		}
	}

	/** 改变英雄属性值 */
	public int takeDamage(Damage damage) {
		DamageEffecter effecter = DamageEffecterFactory.effecterBuilder(damage);
		effecter.exec(this, damage);
		if (isDie()) {
			onDie(damage.getSource());
		}
		addHatred(damage);
		return damage.getDamageValue();
	}

	/**
	 * 添加伤害
	 * 
	 * @param damage
	 */
	protected void addHatred(Damage damage) {

	}

	/** 执行死亡时操作 */
	public boolean onDie(Living killer) {
		synchronized (dieLock) {
			if (this.livingState == DIE) {
				return false;
			}
			this.livingState = DIE;
		}
		clearWorkBuffer();
		// sendChangeStatuMsg(LIVING, livingState);死亡状态不推，客户端自己判断
		dieTime = System.currentTimeMillis();
		return true;
	}

	public void addSkill(Skill skill) {
		this.drivingSkills.put(skill.getSkillId(), skill);
		SkillInfo skillTempateInfo = skill.getSkillinfo();
		if (skillTempateInfo != null) {
			String key = skillTempateInfo.getMasterType() + "_" + skillTempateInfo.getSonType() + "_" + skillTempateInfo.getGrandsonType();
			this.mapSkill.put(key, skill);
		}
	}

	/**
	 * 获取场景对象的外观详细信息
	 * 
	 * @return
	 */
	public BattleLivingInfoMsg.Builder getBattlePlayerInfoMsg() {
		if (cachBattleInfoPacket == null) {
			cachBattleInfoPacket = BattleLivingInfoMsg.newBuilder();
		}
		cachBattleInfoPacket.setLivingId(getId());
		cachBattleInfoPacket.setPlayerId(getArmyId());
		cachBattleInfoPacket.setType(getType());
		if (simpleInfo != null) {
			cachBattleInfoPacket.setNickName(simpleInfo.getNickName());
			cachBattleInfoPacket.setLevel(simpleInfo.getLevel());
			cachBattleInfoPacket.setVipLevel(simpleInfo.getVipLevel());
			cachBattleInfoPacket.setFight(simpleInfo.getFight());
			cachBattleInfoPacket.setSkinId(simpleInfo.getSkinId());
			cachBattleInfoPacket.setFashionId(simpleInfo.getFashionId());
			cachBattleInfoPacket.setWeaponId(simpleInfo.getWeaponId());
			cachBattleInfoPacket.setMountId(simpleInfo.getMountId());
			cachBattleInfoPacket.setMagicWeaponId(simpleInfo.getMagicWeaponId());
			cachBattleInfoPacket.setWingId(simpleInfo.getWingId());
			cachBattleInfoPacket.setWeaponAwaken(simpleInfo.getWeaponAwaken());
		} else {
			cachBattleInfoPacket.setSkinId(getSkin());
		}

		if (getPostion() != null) {
			cachBattleInfoPacket.setPostion(Vector3BuilderHelper.build(getPostion()));
		} else {
			cachBattleInfoPacket.setPostion(Vector3BuilderHelper.build(Vector3.Invalid));
		}
		cachBattleInfoPacket.setTarget(Vector3BuilderHelper.build(getTargetPostion()));
		cachBattleInfoPacket.setLiveState(livingState);
		cachBattleInfoPacket.setSoulState(isSoulState ? 1 : 0);
		// }

		List<Buffer> toalBuffer = new ArrayList<>();
		for (List<Buffer> pBuffers : permanentBuffer.values()) {
			toalBuffer.addAll(pBuffers);
		}
		for (List<Buffer> wBuffers : workBuffers.values()) {
			toalBuffer.addAll(wBuffers);
		}
		for (Buffer buffer : toalBuffer) {
			BufferMsg.Builder bufferMsg = BufferMsg.newBuilder();
			buffer.writeProto(bufferMsg);
			cachBattleInfoPacket.addBufferList(bufferMsg);
		}

		// for (Skill skill : drivingSkills.values()) {
		// cachBattleInfoPacket.addSkills(skill.getSkillId());
		// }

		for (Skill skill : mapSkill.values()) {
			cachBattleInfoPacket.addSkills(skill.getSkillId());
		}

		for (EnumAttr attr : propertys) {
			PropertyMsg.Builder pmsg = PropertyMsg.newBuilder();
			pmsg.setTotalPoint(this.getProperty(attr.getValue()));
			pmsg.setType(attr.getValue());
			cachBattleInfoPacket.addPropertis(pmsg);
		}
		BattleLivingInfoMsg.Builder msg = this.cachBattleInfoPacket;
		this.cachBattleInfoPacket = null;

		return msg;
	}

	/**
	 * 获取场景对象的快照信息
	 * 
	 * @return
	 */
	public PlayerAttSnapMsg.Builder getAttSnapMsg() {

		if (cacheAttSnapPacker == null) {
			cacheAttSnapPacker = PlayerAttSnapMsg.newBuilder();
			cacheAttSnapPacker.setPlayerId(id);
		}
		cacheAttSnapPacker.setType(getType());
		cacheAttSnapPacker.setSkinId(getSkin());
		cacheAttSnapPacker.setPostion(Vector3BuilderHelper.build(getPostion()));
		cacheAttSnapPacker.setTarget(Vector3BuilderHelper.build(getTargetPostion()));
		if (node != null) {
			cacheAttSnapPacker.setBornNodeId(node.getSpwanId());
		}
		return cacheAttSnapPacker;
	}

	/**
	 * 获取附近所有对象集合
	 * 
	 * @return
	 */
	public Set<Integer> getNears(Selector selector) {
		Set<Integer> ret = new HashSet<Integer>();
		if (postion == null) {
			return ret;
		}
		if (field == null) {
			return ret;
		}
		GridCoord coord = null;
		try {
			coord = field.getGrid().getGridCrood(postion.x, postion.z);
		} catch (Exception e) {
			// Log.error("position.x" + postion.x + " position.z" + postion.z, e);
			return ret;
		}
		for (int i = 0; i < Grid.GRID9.length; i++) {
			int[] offset = Grid.GRID9[i];
			int tmpCoordX = coord.X + offset[0];
			int tmpCoordZ = coord.Z + offset[1];
			// if (tmpCoordX < 0 || tmpCoordZ < 0 || tmpCoordX >
			// field.getGrid().GridX || tmpCoordZ > field.getGrid().GridZ)
			// continue;
			GridItem gi = field.getGrid().getGridItem(tmpCoordX, tmpCoordZ);
			if (gi == null)
				continue;
			// Set<Entry<Integer, Set<Integer>>> set = gi.getLivings().entrySet();
			// Iterator<Entry<Integer, Set<Integer>>> it = set.iterator();

			for (Entry<Integer, Set<Integer>> entry : gi.getLivings().entrySet()) {
				// }
				//
				// while (it.hasNext()) {
				// Map.Entry<Integer, Set<Integer>> entry = (Map.Entry<Integer, Set<Integer>>) it.next();

				if (selector != null) {
					// 选择器中排除类型
					if (!selector.selectorType(entry.getKey()))
						continue;
				}
				Set<Integer> entryList = entry.getValue();
				List<Integer> tmp = new ArrayList<Integer>();
				tmp.addAll(entryList);
				for (int j = 0; j < tmp.size(); j++) {
					if (tmp.get(j) == null)
						continue;
					int other = tmp.get(j);
					Living nearLiving = field.getLiving(other);
					if (nearLiving == null) {
						continue;
					}
					if (selector != null) {
						// 选择器中排除id
						if (!selector.selectorid(other)) {
							continue;
						}
						if (!selector.canSee(nearLiving)) {
							continue;
						}

					}
					if (other == id) {
						continue;
					}

					if (selector != null) {
						if (!selector.selectorProtection(nearLiving.protection)) {
							continue;
						}
					}
					ret.add(other);
				}
			}
		}
		return ret;
	}

	public void destory() {
		this.livingState = DISTORY;
	}

	public void setProperty(EnumAttr attr, long value) {
		/* 不允许出现负属性 */
		if (value < 0) {
			value = 0;
		}
		switch (attr) {
		case CUR_SOUL:
			if (value > this.getMaxSoul()) {
				value = this.getMaxSoul();
			}
			this.setCurSoul((int) value);
			break;
		case MAX_SOUL:
			this.setMaxSoul((int) value);
			break;
		case SOUL:
			this.setInitSoul((int) value);
			refreshProperties(attr.getValue());
			break;
		case BLOOD:
			this.setInitBlood((int) value);
			refreshProperties(attr.getValue());
			break;
		case MAX_BLOOD:
			this.setMaxBlood((int) value);
			break;
		case CUR_BLOOD:
			if (value > this.getMaxBlood()) {
				value = this.getMaxBlood();
			}
			this.setCurBlood((int) value);
			break;
		case MANA:
			this.setMana((int) value);
			break;
		case ATTACK:
			this.setInitAttack((int) value);
			refreshProperties(attr.getValue());
			break;
		case DEFENCE:
			this.setInitDefence((int) value);
			refreshProperties(attr.getValue());
			break;
		case SOUL_ATTACK:
			this.setInitSoulAttack((int) value);
			refreshProperties(attr.getValue());
			break;
		case SOUL_DEFENCE:
			this.setInitSoulDefence((int) value);
			refreshProperties(attr.getValue());
			break;
		case ACCURATE:
			this.setInitAccurate((int) value);
			refreshProperties(attr.getValue());
			break;
		case DODGE:
			this.setInitDodge((int) value);
			refreshProperties(attr.getValue());
			break;
		case CRIT:
			this.setInitCrit((int) value);
			refreshProperties(attr.getValue());
			break;
		case CRIT_DEFENCE:
			this.setCritDefence((int) value);
			break;
		case CRIT_ADDTION:
			this.setCritAddtion((int) value);
			break;
		case CRIT_CUT:
			this.setCritCut((int) value);
			break;
		case ATTACK_ADDTION:
			this.setAttackAddtion((int) value);
			break;
		case ATTACK_CUT:
			this.setAttackCut((int) value);
			break;
		case SOUL_ATTACK_ADDTION:
			this.setSoulAttackAddtion((int) value);
			break;
		case SOUL_ATTACK_CUT:
			this.setSoulAttackCut((int) value);
			break;
		case REGAIN_SOUL:
			this.setRegainSoul((int) value);
			break;
		case REGAIN_BLOOD:
			this.setRegainBlood((int) value);
			break;
		case METAL:
			this.setMetal((int) value);
			break;
		case WOOD:
			this.setWood((int) value);
			break;
		case WATER:
			this.setWater((int) value);
			break;
		case FIRE:
			this.setFire((int) value);
			break;
		case EARTH:
			this.setEarth((int) value);
			break;
		case METAL_DEFENCE:
			this.setMetalDefence((int) value);
			break;
		case WOOD_DEFENCE:
			this.setWoodDefence((int) value);
			break;
		case WATER_DEFENCE:
			this.setWaterDefence((int) value);
			break;
		case FIRE_DEFENCE:
			this.setFireDefence((int) value);
			break;
		case EARTH_DEFENCE:
			this.setEarthDefence((int) value);
			break;
		case SPEED:
			this.setSpeed((int) value);
			break;
		case TEAM_ID:
			this.setTeamId((int) value);
			break;
		case PK_VAL:
			this.setPkVal((int) value);
			break;
		case BATTLE_MODE:
			this.setBattleMode((int) value);
			break;
		case Weapon:
			this.simpleInfo.setWeaponId((int) value);
			break;
		case SOUL_EXP:
			this.setSoulExp(value);
			break;
		default:
			break;
		}
	}

	public int getProperty(int type) {
		EnumAttr attr = EnumAttr.getEnumAttrByValue(type);
		switch (attr) {
		case CUR_SOUL:
			return this.getCurSoul();
		case MAX_SOUL:
			return this.getMaxSoul();
		case SOUL:
			return this.getInitSoul();
		case BLOOD:
			return this.getInitBlood();
		case MAX_BLOOD:
			return this.getMaxBlood();
		case CUR_BLOOD:
			return this.getCurBlood();
		case MANA:
			return this.getMana();
		case ATTACK:
			return this.getAttack();
		case DEFENCE:
			return this.getDefence();
		case SOUL_ATTACK:
			return this.getSoulAttack();
		case SOUL_DEFENCE:
			return this.getSoulDefence();
		case ACCURATE:
			return this.getAccurate();
		case DODGE:
			return this.getDodge();
		case CRIT:
			return this.getCrit();
		case CRIT_DEFENCE:
			return this.getCritDefence();
		case CRIT_ADDTION:
			return this.getCritAddtion();
		case CRIT_CUT:
			return this.getCritCut();
		case ATTACK_ADDTION:
			return this.getAttackAddtion();
		case ATTACK_CUT:
			return this.getAttackCut();
		case SOUL_ATTACK_ADDTION:
			return this.getSoulAttackAddtion();
		case SOUL_ATTACK_CUT:
			return this.getSoulAttackCut();
		case REGAIN_SOUL:
			return this.getRegainSoul();
		case REGAIN_BLOOD:
			return this.getRegainBlood();
		case METAL:
			return this.getMetal();
		case WOOD:
			return this.getWood();
		case WATER:
			return this.getWater();
		case FIRE:
			return this.getFire();
		case EARTH:
			return this.getEarth();
		case METAL_DEFENCE:
			return this.getMetalDefence();
		case WOOD_DEFENCE:
			return this.getWoodDefence();
		case WATER_DEFENCE:
			return this.getWaterDefence();
		case FIRE_DEFENCE:
			return this.getFireDefence();
		case EARTH_DEFENCE:
			return this.getEarthDefence();
		case SPEED:
			return this.getSpeed();
		case TEAM_ID:
			return this.getTeamId();
		case PK_VAL:
			return this.getPkVal();
		case BATTLE_MODE:
			return this.getBattleMode();
		default:
			return 0;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSkin() {
		return skin;
	}

	public void setSkin(int skin) {
		this.skin = skin;
	}

	public SimplePlayerInfo getSimpleInfo() {
		return simpleInfo;
	}

	public void setSimpleInfo(SimplePlayerInfo simpleInfo) {
		this.skin = simpleInfo.getSkinId();
		this.simpleInfo = simpleInfo;
	}

	/* 切换战斗状态 */
	public void changeSoulState(boolean state) {
		isSoulState = state;
		sendChangeStatuMsg(FIGHT_STATU, state ? 1 : 0);
	}

	/* 闪名切换 */
	public void changeFlickerName(boolean state) {
		sendChangeStatuMsg(BATTLE_MODE, state ? 1 : 0);
	}

	public void setSoulState(boolean isSoulState) {
		this.isSoulState = isSoulState;
	}

	public boolean isSoulState() {
		return isSoulState;
	}

	public long getDieTime() {
		return dieTime;
	}

	public int lessBlood() {
		return getMaxBlood() - getCurBlood();
	}

	public int lessSoul() {
		return getMaxSoul() - getCurSoul();
	}

	public int getPkVal() {
		return pkVal;
	}

	public void setPkVal(int pkVal) {
		this.pkVal = pkVal;
	}

	public int getBattleMode() {
		return battleMode;
	}

	public void setBattleMode(int battleMode) {
		this.battleMode = battleMode;
	}

	public void exeWorkBuffer() {
		List<Buffer> wbuff = imageBuffs();
		if (wbuff != null && wbuff.size() != 0) {
			List<Damage> damages = new ArrayList<>();
			for (Buffer buff : wbuff) {
				Damage damage1 = new Damage(this, buff.getSource());
				Damage damage2 = new Damage(this, buff.getSource());
				if (buff.checkValid() && buff.execute(null, damage1, damage2, ExecWayType.TIME_LINE)) {
					damages.add(damage1);
					damages.add(damage2);
					takeDamage(damage1);
					takeDamage(damage2);
				}
			}
			if (damages.size() > 0) {
				DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
				damagesPb.setAttackId(-1);
				for (Damage damage : damages) {
					DamageMsg.Builder dmsg = DamageMsg.newBuilder();
					damage.writeProto(dmsg);
					damagesPb.addDamages(dmsg);
				}
				Set<Integer> players = getNears(new PlayerSelectorHelper(this));
				// 添加自己
				players.add(getArmyId());

				BroadUtil.sendBroadcastPacket(players, Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
			}
		}
	}

	public void execWayBuffer(AttackOrder order, int execWay) {
		List<Buffer> buffers = getExeWayBuffers(execWay);
		if (buffers != null && buffers.size() != 0) {
			List<Damage> damages = new ArrayList<>();
			for (Buffer buff : buffers) {
				Damage damage1 = new Damage(this, buff.getSource());
				Damage damage2 = new Damage(this, buff.getSource());
				if (buff.checkValid() && buff.execute(order, damage1, damage2, execWay)) {
					damages.add(damage1);
					damages.add(damage2);
					takeDamage(damage1);
					takeDamage(damage2);
				}
			}
			if (damages.size() > 0) {
				DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
				damagesPb.setAttackId(-1);
				for (Damage damage : damages) {
					DamageMsg.Builder dmsg = DamageMsg.newBuilder();
					damage.writeProto(dmsg);
					damagesPb.addDamages(dmsg);
				}
				Set<Integer> players = getNears(new PlayerSelectorHelper(this));
				// 添加自己
				players.add(getArmyId());

				BroadUtil.sendBroadcastPacket(players, Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
			}
		}
	}

	public void execBuffer(Buffer buff, int execWay) {
		List<Damage> damages = new ArrayList<>();
		Damage damage1 = new Damage(this, buff.getSource());
		Damage damage2 = new Damage(this, buff.getSource());
		boolean isValid = false;
		if (execWay == ExecWayType.REMOVE) {
			isValid = true;
		} else {
			isValid = buff.checkValid();
		}
		if (isValid && buff.execute(null, damage1, damage2, execWay)) {
			damages.add(damage1);
			damages.add(damage2);
			takeDamage(damage1);
			takeDamage(damage2);
		}

		if (damages.size() > 0) {
			DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
			damagesPb.setAttackId(-1);
			for (Damage d : damages) {
				DamageMsg.Builder dmsg = DamageMsg.newBuilder();
				d.writeProto(dmsg);
				damagesPb.addDamages(dmsg);
			}
			Set<Integer> players = getNears(new PlayerSelectorHelper(this));
			// 添加自己
			players.add(getArmyId());
			BroadUtil.sendBroadcastPacket(players, Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
		}
	}

	/* 根据属性类型添加修改的变化，刷新对应属性 */
	public void refreshProperties(int type) {
		// 获取所有影响该属性的buffer
		List<Buffer> buffers = getTypeBuffers(BufferType.ATTR_BODY);
		List<Buffer> invock = new ArrayList<>();
		for (Buffer buffer : buffers) {
			if (buffer.getBufferInfo().getValueType() == type) {
				invock.add(buffer);
			}
		}
		EnumAttr etype = EnumAttr.getEnumAttrByValue(type);
		if (etype == null) {
			log.error("EnumAttr etype is null, type : " + type);
			return;
		}
		// 获取初始属性
		int lastValue = getInitValue(etype);
		// 计算变更值
		int addValue = 0;
		// for (Buffer buffer : invock) {
		// if (buffer.getBufferInfo().getValueType() == type) {
		// int effectValue = buffer.getBufferInfo().getValue() + (int) Math.ceil(lastValue * (buffer.getBufferInfo().getValuePercent() / 10000f));
		// addValue = buffer.calSoullv(effectValue, SoulFuseSkillConfig.EFFECT);
		// }
		// if (buffer.getBufferInfo().getValueType1() == type) {
		// int effectValue = buffer.getBufferInfo().getValue1() + (int) Math.ceil(lastValue * (buffer.getBufferInfo().getValuePercent1() / 10000f));
		// addValue = buffer.calSoullv(effectValue, SoulFuseSkillConfig.EFFECT);
		// }
		// }
		//
		// // 当玩家身上存在魂攻物攻转换buffer时，在计算完毕所有属性加成，再做转换。
		// if (type == EnumAttr.ATTACK.getValue() || type == EnumAttr.SOUL_ATTACK.getValue()) {
		// List<Buffer> convertBuffers = getTypeBuffers(BufferType.ATTACK_COVENT_SOULATTACK);
		// for (Buffer as : convertBuffers) {
		// AttackConvertSoulAttackBuffer A2S = (AttackConvertSoulAttackBuffer) as;
		// addValue += A2S.getResult(type);
		// }
		//
		// List<Buffer> convert2Buffers = getTypeBuffers(BufferType.SOULATTACK_COVENT_ATTACK);
		// for (Buffer sa : convert2Buffers) {
		// SoulAttackConvertAttackBuffer S2A = (SoulAttackConvertAttackBuffer) sa;
		// addValue += S2A.getResult(type);
		// }
		// }

		// 更新终值
		lastValue += addValue;
		setBufferProperty(etype, lastValue);

		PlayerAttUpdateMsg.Builder notifyMsg = PlayerAttUpdateMsg.newBuilder();
		PropertyMsg.Builder speedMsg = PropertyMsg.newBuilder();
		speedMsg.setType(etype.getValue());
		speedMsg.setTotalPoint(lastValue);
		notifyMsg.addAtt(speedMsg);
		notifyMsg.setPlayerId(getId());
		// 通知附近玩家
		Set<Integer> nears = getNears(new PlayerSelectorHelper(this));
		if (this.armyId > 0) {
			nears.add(this.armyId);
		}
		BroadUtil.sendBroadcastPacket(nears, Protocol.MAIN_PLAYER, Protocol.PLAYER_PROPERTY, notifyMsg.build());
	}

	public void clearWorkBuffer() {
		List<Buffer> allbuffers = imageBuffs();
		for (Buffer buff : allbuffers) {
			buff.setState(BufferState.DEAD_REMOVE);
			buff.dispose();
		}
		workBuffers.clear();
		typeBuffers.clear();
		allBuffers.clear();
		livingStatus.clear();
		initState();
	}

	public boolean isFighting() {
		return fightState;
	}

	public void fight() {
		if (!fightState) {
			fightState = true;
		}
		lastFightTm = System.currentTimeMillis();
	}

	/** 脱离战斗 */
	public void leaveFight() {
		if (this.getType() == RoleType.player) {
			changeFlickerName(false);
			((Player) this).setFlashName(false);
		}

		if (!fightState) {
			return;
		}
		fightState = false;
		if (isSoulState() && getCurBlood() > 0) {
			changeSoulState(false);
			List<Damage> damages = new ArrayList<>();
			Damage curBlood = new Damage(this, this);
			curBlood.setDamageType(EnumAttr.CUR_BLOOD.getValue());
			curBlood.setDamageValue(0 - getInitBlood() * 5 / 100);
			curBlood.setCalcType(DamageEffecterType.BLOOD);
			damages.add(curBlood);
			takeDamage(curBlood);
			if (damages.size() > 0) {
				DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
				damagesPb.setAttackId(-1);
				for (Damage d : damages) {
					DamageMsg.Builder dmsg = DamageMsg.newBuilder();
					d.writeProto(dmsg);
					damagesPb.addDamages(dmsg);
				}
				Set<Integer> players = getNears(new PlayerSelectorHelper(this));
				// 添加自己
				players.add(getArmyId());
				for (Integer armyId : players) {
					ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(armyId);
					if (army != null) {
						// PBMessage message = MessageUtil.buildMessage(Protocol.U_G_DAMAGE, damagesPb.build());
						army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
					}
				}
			}
		}

	}

	public void calPkVal() {
		if (this.getField().getFieldInfo().getIsBattle() && this.getPkVal() > 0) {
			this.pkValCalTime = System.currentTimeMillis();
			int changePkVal = MathUtils.randomClamp(1, 5);
			changePkVal = this.getPkVal() - changePkVal < 0 ? 0 : this.getPkVal() - changePkVal;
			this.setPkVal(changePkVal);
			Map<Integer, Long> changeMap = new HashMap<Integer, Long>();
			changeMap.put(EnumAttr.PK_VAL.getValue(), (long) this.getPkVal());
			notifyCenter(changeMap, this.getArmyId());

			List<PropertyMsg> properties = new ArrayList<>();
			PropertyMsg.Builder p = PropertyMsg.newBuilder();
			p.setBasePoint((long) this.getPkVal());
			p.setTotalPoint((long) this.getPkVal());
			p.setType(EnumAttr.PK_VAL.getValue());
			properties.add(p.build());
			updateProperty(this, properties);
		}
	}

	/**
	 * 通知
	 * 
	 * @param changeMap
	 * @param playerId
	 * @return
	 */
	public void notifyCenter(Map<Integer, Long> changeMap, int playerId) {
		PlayerAttUpdateMsg.Builder msg = PlayerAttUpdateMsg.newBuilder();
		msg.setPlayerId(playerId);
		for (int type : changeMap.keySet()) {
			PropertyMsg.Builder attMsg = PropertyMsg.newBuilder();
			attMsg.setType(type);
			attMsg.setTotalPoint(changeMap.get(type));
			msg.addAtt(attMsg);
		}
		// PBMessage pkg = MessageUtil.buildMessage(Protocol.C_PLAYER_UPDATA_PRO, msg);
		// GatewayLinkedSet.send2Server(pkg);

	}

	public long getLastFightTM() {
		return lastFightTm;
	}

	public long getPkValCalTime() {
		return pkValCalTime;
	}

	public boolean canSee(long id) {
		return true;
	}

	public String toString() {
		return "LivingId :" + this.getId() + "  armyId:" + armyId + " type:" + this.type;
	}

	public void sendChangeStatuMsg(int stateType, int stateValue) {
		LivingStateChangeMsg.Builder changeState = LivingStateChangeMsg.newBuilder();
		changeState.setLivingId(id);
		changeState.setStateType(stateType);
		changeState.setStateValue(stateValue);

		Set<Integer> nears = getNears(new PlayerSelectorHelper(this));
		nears.add(armyId);
		for (Integer armyId : nears) {
			ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(armyId);
			if (army != null) {
				army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_LIVINGSTATUS, changeState.build());
			}
		}
		// BroadUtil.sendBroadcastPacket(nears,
		// Protocol.U_LIVING_STATE_CHANGE, changeState.build());
	}

	public List<Buffer> imageBuffs() {
		List<Buffer> wbuff = new ArrayList<>();
		wbuff.addAll(allBuffers.values());
		return wbuff;
	}

	/**
	 * 获取攻击技能
	 * 
	 * @return
	 */
	public Skill getAttackSkill() {
		Skill skill = null;
		if (this.drivingSkills.isEmpty())
			return null;
		// Integer[] keys = this.drivingSkills.keySet().toArray(new Integer[0]);
		// Integer randomKey = MathUtils.randomClamp(0,
		// this.drivingSkills.size() - 1);
		// Integer key = keys[randomKey];
		// skill = this.drivingSkills.get(key);
		List<Map.Entry<Integer, Skill>> skills = new ArrayList<Map.Entry<Integer, Skill>>(this.drivingSkills.entrySet());
		Collections.sort(skills, new Comparator<Map.Entry<Integer, Skill>>() {
			public int compare(Map.Entry<Integer, Skill> o1, Map.Entry<Integer, Skill> o2) {
				return (o2.getValue().getTemplateInfo().getCooldown() - o1.getValue().getTemplateInfo().getCooldown());
			}
		});
		for (int i = 0; i < skills.size(); i++) {
			if (isCooldowning(CoolDownTypes.SKILL, skills.get(i).getValue().getActionId() + "")) {
				if (i == skills.size() - 1)
					skill = skills.get(i).getValue();
				continue;
			}
			skill = skills.get(i).getValue();
			break;
		}
		return skill;
	}

	/** buffer替换 */
	public void overlay(Buffer older, Buffer newer) {
		if (older.getState() == BufferState.INVALID) {
			removeBuffer(older);
			simpleAdd(newer);
			return;
		}
		if (older.getOverlayWay() == BufferOverlayType.REPLACE && newer.getBufferInfo().getLevel() >= older.getBufferInfo().getLevel()) {
			removeBuffer(older);
			simpleAdd(newer);
		}

		if (older.getOverlayWay() == BufferOverlayType.REPLACE_TIME) {
			if (newer.getAliveTime() > older.getAliveTime()) {
				removeBuffer(older);
				simpleAdd(newer);
			}
		}

		if (older.getOverlayWay() == BufferOverlayType.REPLACE_COUNT) {
			older.setState(BufferState.VALID);
			older.setLeftCount(newer.getBufferInfo().getExeCount());
			older.setAliveTime(newer.getAliveTime());
			upBuffer(older);
		}

		if (older.getOverlayWay() == BufferOverlayType.SUPERIMPOSED) {
			older.setState(BufferState.VALID);
			older.setLeftCount(older.getLeftCount() + newer.getBufferInfo().getExeCount());
			older.setAliveTime(newer.getAliveTime());
			upBuffer(older);
		}

		if (older.getOverlayWay() == BufferOverlayType.SUPERIMPOSED_EFFECT) {
			older.setState(BufferState.VALID);
			older.setLeftCount(newer.getBufferInfo().getExeCount());
			older.setAliveTime(newer.getAliveTime());
			older.addPressedNum(1);
			upBuffer(older);
		}
	}

	/** 检查对应状态，false 不可行，true 可行 */
	public boolean checkStatus(EnumBufferState state) {
		AtomicInteger value = livingStatus.get(state);
		if (value != null) {
			return value.get() <= 0;
		}
		return true;
	}

	protected void addLivingState(int stateId) {
		// HeroStatus temp = ServiceManager.getManager().getGameConfigService().getHeroStatus().get(stateId);
		// // 模板不存在
		// if (temp == null) {
		// return;
		// }
		// // 免疫控制
		// if (temp.getType() == LivingStatusTemplateInfo.CONTROL && !checkStatus(EnumBufferState.BE_CONTROL)) {
		// return;
		// }
		// List<EnumBufferState> affectedStates = temp.getAffected();
		// for (EnumBufferState state : affectedStates) {
		// AtomicInteger value = livingStatus.get(state);
		// if (value == null) {
		// log.error("the state is not exists  " + value);
		// continue;
		// }
		// value.incrementAndGet();
		// notityState(state);
		// }
	}

	protected void removeLivingState(int stateId) {
		// LivingStatusTemplateInfo temp = BattleTempMgr.getLSInfo(stateId);
		// // 模板不存在
		// if (temp == null) {
		// return;
		// }
		// List<EnumBufferState> affectedStates = temp.getAffected();
		// for (EnumBufferState state : affectedStates) {
		// AtomicInteger value = livingStatus.get(state);
		// value.decrementAndGet();
		// }
	}

	/**
	 * 添加冷却
	 * 
	 * @param roleId 玩家Id
	 * @param type 类型
	 * @param key 关键字
	 * @param delay 冷却时间
	 */
	public void addCooldown(CoolDownTypes type, String key, long delay) {
		// 初始化冷却关键字
		String cooldownKey = null;
		if (key == null) {
			cooldownKey = type.getValue();
		} else {
			cooldownKey = type.getValue() + "_" + key;
		}
		if (cooldowns.containsKey(cooldownKey)) {
			CoolDown cooldown = cooldowns.get(cooldownKey);
			cooldown.setStart(System.currentTimeMillis());
			cooldown.setDelay(delay);
		} else {
			// 初始化冷却信息
			CoolDown cooldown = new CoolDown();
			cooldown.setType(type.getValue());
			cooldown.setKey(cooldownKey);
			cooldown.setStart(System.currentTimeMillis());
			cooldown.setDelay(delay);

			// 添加冷却
			cooldowns.put(cooldownKey, cooldown);
		}
	}

	/**
	 * 是否在冷却中
	 * 
	 * @param monsterId 玩家Id
	 * @param type 冷却类型
	 * @param key 关键字
	 * @return
	 */
	public boolean isCooldowning(CoolDownTypes type, String key) {
		// 初始化冷却关键字
		String cooldownKey = null;
		if (key == null) {
			cooldownKey = type.getValue();
		} else {
			cooldownKey = type.getValue() + "_" + key;
		}

		// 查看冷却
		if (cooldowns.containsKey(cooldownKey)) {
			CoolDown cooldown = cooldowns.get(cooldownKey);
			// if (this.getId() == 1000000000033L && type ==
			// CoolDownTypes.SKILL)
			// System.out.println("cooldownKey: "+cooldownKey +"
			// cooldown.getDelay:"+cooldown.getDelay()+ "
			// "+(System.currentTimeMillis() > cooldown.getStart() +
			// cooldown.getDelay()));

			if (System.currentTimeMillis() > cooldown.getStart() + cooldown.getDelay()) {
				// 冷却时间已经结束
				// obj.getCooldowns().remove(cooldownKey);
				// cooldownPool.put(cooldown);
				return false;
			} else {
				return true;
			}
		}

		return false;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	/** 自杀 */
	public void suicide() {
		List<Damage> damages = new ArrayList<>();

		Damage soul = new Damage(this, this);
		soul.setDamageType(EnumAttr.CUR_SOUL.getValue());
		soul.setDamageValue(Integer.MAX_VALUE);
		soul.setSource(this);
		damages.add(soul);
		takeDamage(soul);

		DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
		damagesPb.setAttackId(-1);
		for (Damage d : damages) {
			DamageMsg.Builder dmsg = DamageMsg.newBuilder();
			d.writeProto(dmsg);
			damagesPb.addDamages(dmsg);
		}
		Set<Integer> players = getNears(new PlayerSelectorHelper(this));
		// 添加自己
		players.add(getArmyId());
		for (Integer armyId : players) {
			ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(armyId);
			// PBMessage message = MessageUtil.buildMessage(Protocol.U_G_DAMAGE, damagesPb.build());
			if (army != null) {
				army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
			}
		}
	}

	public void addCurBlood(int addValue, int type, int fromType, long fromId) {
		List<Damage> damages = new ArrayList<>();

		Damage blood = new Damage(this, this);
		blood.setDamageType(EnumAttr.CUR_BLOOD.getValue());
		blood.setDamageValue(-addValue);
		blood.setSource(this);
		blood.setCalcType(type);
		blood.setFromType(fromType);
		blood.setFromId(fromId);
		damages.add(blood);
		takeDamage(blood);

		DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
		damagesPb.setAttackId(-1);
		for (Damage d : damages) {
			DamageMsg.Builder dmsg = DamageMsg.newBuilder();
			d.writeProto(dmsg);
			damagesPb.addDamages(dmsg);
		}
		Set<Integer> players = getNears(new PlayerSelectorHelper(this));
		// 添加自己
		players.add(getArmyId());

		for (Integer armyId : players) {
			ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(armyId);
			if (army != null) {
				army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
			}
		}
	}

	public void addCurSoul(int addValue, int type, int fromType, long fromId) {
		List<Damage> damages = new ArrayList<>();

		Damage soul = new Damage(this, this);
		soul.setDamageType(EnumAttr.CUR_SOUL.getValue());
		soul.setDamageValue(-addValue);
		soul.setCalcType(type);
		soul.setSource(this);
		soul.setFromType(fromType);
		soul.setFromId(fromId);
		damages.add(soul);
		takeDamage(soul);

		DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
		damagesPb.setAttackId(-1);
		for (Damage d : damages) {
			DamageMsg.Builder dmsg = DamageMsg.newBuilder();
			d.writeProto(dmsg);
			damagesPb.addDamages(dmsg);
		}
		Set<Integer> players = getNears(new PlayerSelectorHelper(this));
		// 添加自己
		players.add(getArmyId());
		for (Integer armyId : players) {
			ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(armyId);
			if (army != null) {
				army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
			}
		}
	}

	public int getInitValue(EnumAttr type) {
		switch (type) {
		case BLOOD:
			return initBlood;
		case SOUL:
			return initSoul;
		case ATTACK:
			return initAttack;
		case DEFENCE:
			return initDefence;
		case SOUL_ATTACK:
			return initSoulAttack;
		case SOUL_DEFENCE:
			return initSoulDefence;
		case ACCURATE:
			return initAccurate;
		case DODGE:
			return initDodge;
		case CRIT:
			return initCrit;
		default:
			log.error("not suppot type :" + type.getValue());
			return getProperty(type.getValue());
		}
	}

	public void setBufferProperty(EnumAttr attr, long value) {
		/* 不允许出现负属性 */
		if (value < 0) {
			value = 0;
		}
		switch (attr) {
		case MAX_SOUL:
			this.setMaxSoul((int) value);
			break;
		case MAX_BLOOD:
			this.setMaxBlood((int) value);
			break;
		case ATTACK:
			this.setAttack((int) value);
			break;
		case DEFENCE:
			this.setDefence((int) value);
			break;
		case SOUL_ATTACK:
			this.setSoulAttack((int) value);
			break;
		case SOUL_DEFENCE:
			this.setSoulDefence((int) value);
			break;
		default:
			break;
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefence() {
		return defence;
	}

	public void setDefence(int defence) {
		this.defence = defence;
	}

	public int getSoulAttack() {
		return soulAttack;
	}

	public void setSoulAttack(int soulAttack) {
		this.soulAttack = soulAttack;
	}

	public int getSoulDefence() {
		return soulDefence;
	}

	public void setSoulDefence(int soulDefence) {
		this.soulDefence = soulDefence;
	}

	public int getAccurate() {
		return accurate;
	}

	public void setAccurate(int accurate) {
		this.accurate = accurate;
	}

	public int getDodge() {
		return dodge;
	}

	public void setDodge(int dodge) {
		this.dodge = dodge;
	}

	public int getCrit() {
		return crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public int getCritDefence() {
		return critDefence;
	}

	public void setCritDefence(int critDefence) {
		this.critDefence = critDefence;
	}

	public int getCritAddtion() {
		return critAddtion;
	}

	public void setCritAddtion(int critAddtion) {
		this.critAddtion = critAddtion;
	}

	public int getCritCut() {
		return critCut;
	}

	public void setCritCut(int critCut) {
		this.critCut = critCut;
	}

	public int getAttackAddtion() {
		return attackAddtion;
	}

	public void setAttackAddtion(int attackAddtion) {
		this.attackAddtion = attackAddtion;
	}

	public int getAttackCut() {
		return attackCut;
	}

	public void setAttackCut(int attackCut) {
		this.attackCut = attackCut;
	}

	public int getSoulAttackAddtion() {
		return soulAttackAddtion;
	}

	public void setSoulAttackAddtion(int soulAttackAddtion) {
		this.soulAttackAddtion = soulAttackAddtion;
	}

	public int getSoulAttackCut() {
		return soulAttackCut;
	}

	public void setSoulAttackCut(int soulAttackCut) {
		this.soulAttackCut = soulAttackCut;
	}

	public int getRegainSoul() {
		return regainSoul;
	}

	public void setRegainSoul(int regainSoul) {
		this.regainSoul = regainSoul;
	}

	public int getRegainBlood() {
		return regainBlood;
	}

	public void setRegainBlood(int regainBlood) {
		this.regainBlood = regainBlood;
	}

	public int getMetal() {
		return metal;
	}

	public void setMetal(int metal) {
		this.metal = metal;
	}

	public int getWood() {
		return wood;
	}

	public void setWood(int wood) {
		this.wood = wood;
	}

	public int getWater() {
		return water;
	}

	public void setWater(int water) {
		this.water = water;
	}

	public int getFire() {
		return fire;
	}

	public void setFire(int fire) {
		this.fire = fire;
	}

	public int getEarth() {
		return earth;
	}

	public void setEarth(int earth) {
		this.earth = earth;
	}

	public int getMetalDefence() {
		return metalDefence;
	}

	public void setMetalDefence(int metalDefence) {
		this.metalDefence = metalDefence;
	}

	public int getWoodDefence() {
		return woodDefence;
	}

	public void setWoodDefence(int woodDefence) {
		this.woodDefence = woodDefence;
	}

	public int getWaterDefence() {
		return waterDefence;
	}

	public void setWaterDefence(int waterDefence) {
		this.waterDefence = waterDefence;
	}

	public int getFireDefence() {
		return fireDefence;
	}

	public void setFireDefence(int fireDefence) {
		this.fireDefence = fireDefence;
	}

	public int getEarthDefence() {
		return earthDefence;
	}

	public void setEarthDefence(int earthDefence) {
		this.earthDefence = earthDefence;
	}

	public int getSpeed() {
		return this.speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public int getInitSoul() {
		return initSoul;
	}

	public int getMaxSoul() {
		return maxSoul;
	}

	public void setMaxSoul(int maxSoul) {
		this.maxSoul = maxSoul;
	}

	public int getCurSoul() {
		return curSoul;
	}

	public void setCurSoul(int curSoul) {
		this.curSoul = curSoul;
	}

	public int getInitBlood() {
		return initBlood;
	}

	public void setInitBlood(int initBlood) {
		this.initBlood = initBlood;
	}

	public int getMaxBlood() {
		return maxBlood;
	}

	public void setMaxBlood(int maxBlood) {
		this.maxBlood = maxBlood;
	}

	public int getCurBlood() {
		return curBlood;
	}

	public void setCurBlood(int curBlood) {
		this.curBlood = curBlood;
		if (curBlood == 0 && isSoulState() == false) {
			changeSoulState(true);
		}

		if (curBlood == getMaxBlood() && isSoulState() == true) {
			changeSoulState(false);
		}
	}

	public Map<Integer, Skill> getDrivingSkills() {
		return drivingSkills;
	}

	public void setDrivingSkills(Map<Integer, Skill> drivingSkills) {
		this.drivingSkills = drivingSkills;
	}

	public void setInitSoul(int initSoul) {
		this.initSoul = initSoul;
	}

	public boolean isPlayer() {
		return this.type == RoleConstants.RoleType.player;
	}

	public boolean isProtection() {
		return protection;
	}

	public void setProtection(boolean protection) {
		this.protection = protection;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	public int getArmyId() {
		return armyId;
	}

	public void setArmyId(int armyId) {
		this.armyId = armyId;
	}

	public int getLivingState() {
		return livingState;
	}

	public void setLivingState(int livingState) {
		this.livingState = livingState;
	}

	public long getRestoreTime() {
		return restoreTime;
	}

	public void setRestoreTime(long restoreTime) {
		this.restoreTime = restoreTime;
	}

	public void setInitAttack(int initAttack) {
		this.initAttack = initAttack;
	}

	public void setInitDefence(int initDefence) {
		this.initDefence = initDefence;
	}

	public void setInitSoulAttack(int initSoulAttack) {
		this.initSoulAttack = initSoulAttack;
	}

	public void setInitSoulDefence(int initSoulDefence) {
		this.initSoulDefence = initSoulDefence;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public boolean isClear() {
		return false;
	}

	public void setInitAccurate(int initAccurate) {
		this.initAccurate = initAccurate;
	}

	public void setInitDodge(int initDodge) {
		this.initDodge = initDodge;
	}

	public void setInitCrit(int initCrit) {
		this.initCrit = initCrit;
	}

	public void clearData() {
		field = null;
		node = null;
		cachBattleInfoPacket = null;
		cacheAttSnapPacker = null;
		drivingSkills.clear();
		mapSkill.clear();
		permanentBuffer.clear();
		workBuffers.clear();
		allBuffers.clear();
		livingStatus.clear();
		cooldowns.clear();
		livingState = DISTORY;
	}

	public long getSoulExp() {
		return soulExp;
	}

	public void setSoulExp(long soulExp) {
		this.soulExp = soulExp;
	}

	protected void notityState(EnumBufferState state) {

	}

	public AI getAi() {
		return ai;
	}

	public UpdatePosition getUpdatePosition() {
		return updatePosition;
	}

}
