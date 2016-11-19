package com.app.empire.scene.service.role.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.app.empire.protocol.data.scene.world.HeroInfoMsg;
import com.chuangyou.common.protobuf.pb.army.PropertyMsgProto.PropertyMsg;
import com.chuangyou.common.protobuf.pb.battle.BattleLivingInfoMsgProto.BattleLivingInfoMsg;
import com.chuangyou.common.protobuf.pb.battle.BattleLivingInfoMsgProto.BattleLivingInfoMsg.Builder;
import com.chuangyou.common.protobuf.pb.battle.DamageListMsgProtocol.DamageListMsg;
import com.chuangyou.common.protobuf.pb.battle.DamageMsgProto.DamageMsg;
import com.chuangyou.common.protobuf.pb.soul.FuseSkillProto.FuseSkillMsg;
import com.chuangyou.common.util.Log;
import com.app.empire.scene.util.MathUtils;
import com.app.empire.scene.service.battle.action.HeroPollingAction;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferFactory;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.mgr.BattleTempMgr;
import com.chuangyou.xianni.battle.skill.FuseSkillVo;
import com.app.empire.scene.service.battle.skill.Skill;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.campaign.node.CampaignNodeDecorator;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;
import com.chuangyou.xianni.common.templete.SystemConfigTemplateMgr;
import com.app.empire.scene.constant.BattleModeCode;
import com.app.empire.scene.constant.EnumAttr;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.entity.equip.EquipAwakenCfg;
import com.chuangyou.xianni.entity.mount.MountGradeCfg;
import com.chuangyou.xianni.entity.skill.SkillActionTemplateInfo;
import com.chuangyou.xianni.entity.skill.SkillTempateInfo;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.equip.template.EquipTemplateMgr;
import com.app.empire.scene.util.exec.DelayAction;
import com.app.empire.scene.util.exec.ThreadManager;
import com.chuangyou.xianni.mount.MountTempleteMgr;
import com.chuangyou.xianni.proto.BroadcastUtil;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.app.empire.scene.service.role.action.RevivalPlayerAction;
import com.app.empire.scene.service.role.helper.RoleConstants.RoleType;
import com.app.empire.scene.service.warfield.field.Field;
import com.app.empire.scene.service.warfield.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.service.warfield.spawn.BeadMonsterSpawnNode;
import com.app.empire.scene.service.warfield.spawn.PerareState;
import com.app.empire.scene.service.warfield.spawn.SpwanNode;
import com.app.empire.scene.service.warfield.template.SpawnTemplateMgr;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.service.world.WorldMgr;

public class Player extends ActiveLiving {
	/** 是否复活中 */
	private volatile boolean revivaling = false;
	/**
	 * 玩家坐骑状态 0未乘骑 1乘骑坐骑
	 */
	private int mountState = 1;
	private List<Integer> monsterRefreshIdList = new ArrayList<Integer>();
	private boolean flashName = false;

	/** 副本buffer */
	private List<Buffer> campaignBuffers = new ArrayList<>();

	/** 魂幡携带buffer */
	private Map<Integer, FuseSkillVo> fuseSkillVos = new HashMap<>();

	/** 魂幡buffers */
	private Buffer[] fuseSkillBuffers = new Buffer[4];

	/** 武器携带buffer */
	private Buffer weaponBuffer;

	/** 武器技能(觉醒获得) */
	private int weaponBuffId = 0;
	/**
	 * 魂幡等级
	 */
	private int soulLv = 0;

	public Player(long playerId) {
		super(playerId, playerId);
		// 每个人物身上有自调度，人物退出时候清理。(注意：添加此调度，必须有对应的销毁对象后清理)
		setType(RoleType.player);
		HeroPollingAction heroAction = new HeroPollingAction(this);
		this.enDelayQueue(heroAction);
	}

	public void readHeroInfo(HeroInfoMsg hero) {
		readProperty(hero.getPropertis());
		readSkillInfo(hero);
		this.setSoulLv(hero.getSoulLv());
	}

	public void updateHeroInfo(HeroInfoMsg hero) {
		readProperty(hero.getPropertis());
		readSkillInfo(hero);

		Set<Long> nears = getNears(new PlayerSelectorHelper(this));
		nears.add(getArmyId());
		BroadcastUtil.sendBroadcastPacket(nears, Protocol.U_G_BATTLEPLAYERINFO, getBattlePlayerInfoMsg().build());
	}

	public void updateWeaponBuff() {
		if (simpleInfo.getWeaponId() == 0 || simpleInfo.getWeaponAwaken() == 0) {
			this.setWeaponBuffId(0);
		} else {
			EquipAwakenCfg cfg = EquipTemplateMgr.getAwakenMap().get(simpleInfo.getWeaponId() * 10000 + simpleInfo.getWeaponAwaken() * 100 + 0);
			if (cfg != null) {
				this.setWeaponBuffId(cfg.getSkillId());
			} else {
				this.setWeaponBuffId(0);
			}

		}
		addWeaponBuffer(this.getWeaponBuffId());
	}

	@Override
	public boolean onDie(Living source) {
		if (super.onDie(source)) {
			DieAction die = new DieAction(this, source, 1000);
			die.getActionQueue().enDelayQueue(die);

			if (field != null && field.getCampaignId() > 0) {
				Campaign campaign = CampaignMgr.getCampagin(field.getCampaignId());
				if (campaign != null) {
					campaign.notifyTaskEvent(CTBaseCondition.LESS_DEAD_COUNT, 1);
				}
			}
		}

		return true;
	}

	class DieAction extends DelayAction {
		Living deather;
		Living source;

		public DieAction(Living deather, Living source, int delay) {
			super(source, delay);
			this.deather = deather;
			this.source = source;
		}

		@Override
		public void execute() {
			ArmyProxy army = WorldMgr.getArmy(getArmyId());
			if (army == null) {
				Log.error("not find army when player die,playerId :" + getArmyId());
				return;
			}
			if (revivaling == false) {
				RevivalPlayerAction revival = new RevivalPlayerAction(army);
				ThreadManager.actionExecutor.enDelayQueue(revival);
				revivaling = true;
			}

			// 攻击源处理
			if (source.getBattleMode() == BattleModeCode.warBattleMode && getBattleMode() == BattleModeCode.peaceBattleMode) {// 增加pk值
				source.setPkVal(source.getPkVal() + 1000);
				// 通知
				Map<Integer, Long> changeMap = new HashMap<Integer, Long>();
				changeMap.put(EnumAttr.PK_VAL.getValue(), (long) source.getPkVal());
				notifyCenter(changeMap, source.getArmyId());

				List<PropertyMsg> properties = new ArrayList<>();
				PropertyMsg.Builder p = PropertyMsg.newBuilder();
				p.setBasePoint((long) source.getPkVal());
				p.setTotalPoint((long) source.getPkVal());
				p.setType(EnumAttr.PK_VAL.getValue());
				properties.add(p.build());
				updateProperty(source, properties);
			}

			// 自己
			List<PropertyMsg> properties = new ArrayList<>();
			Map<Integer, Long> changeMap = new HashMap<Integer, Long>();

			int changePkVal = 0;// 减少pk值
			if (getColour(getPkVal()) == BattleModeCode.yellow) {
				changePkVal = MathUtils.randomClamp(10, 20);
				notifyCenter(2, (int) source.getArmyId(), (int) getArmyId());
			} else if (getColour(getPkVal()) == BattleModeCode.red) {
				changePkVal = MathUtils.randomClamp(40, 80);
				notifyCenter(2, (int) source.getArmyId(), (int) getArmyId());
			}
			if (changePkVal > 0) {
				changePkVal = getPkVal() - changePkVal < 0 ? 0 : getPkVal() - changePkVal;
				setPkVal(changePkVal);
				PropertyMsg.Builder p = PropertyMsg.newBuilder();
				p.setBasePoint(changePkVal);
				p.setTotalPoint(changePkVal);
				p.setType(EnumAttr.PK_VAL.getValue());
				properties.add(p.build());
				changeMap.put(EnumAttr.PK_VAL.getValue(), (long) changePkVal);
				notifyCenter(changeMap, getArmyId());
				updateProperty(deather, properties);
			}
			calPKValue(source, deather);

		}

	}

	/**
	 * 获取颜色级别
	 * 
	 * @param val
	 * @return
	 */
	public int getColour(int val) {
		int minRed = SystemConfigTemplateMgr.getIntValue("pk.colour.minRed");
		int minYellow = SystemConfigTemplateMgr.getIntValue("pk.colour.minYellow");

		if (val >= minRed) {
			return BattleModeCode.red;
		} else if (val >= minYellow) {
			return BattleModeCode.yellow;
		}
		return BattleModeCode.white;
	}

	/* 满血复活 */
	public boolean renascence() {
		if (this.livingState == ALIVE) {
			return false;
		}

		this.livingState = ALIVE;
		sendChangeStatuMsg(LIVING, livingState);
		List<Damage> damages = new ArrayList<>();
		Damage curSoul = new Damage(this, this);
		curSoul.setDamageType(EnumAttr.CUR_SOUL.getValue());
		curSoul.setDamageValue(0 - getInitSoul());
		damages.add(curSoul);
		takeDamage(curSoul);

		Damage curBlood = new Damage(this, this);
		curBlood.setDamageType(EnumAttr.CUR_BLOOD.getValue());
		curBlood.setDamageValue(0 - getInitBlood());
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
			Set<Long> players = getNears(new PlayerSelectorHelper(this));
			// 添加自己
			players.add(getArmyId());
			for (Long armyId : players) {
				ArmyProxy army = WorldMgr.getArmy(armyId);
				PBMessage message = MessageUtil.buildMessage(Protocol.U_G_DAMAGE, damagesPb.build());
				if (army != null) {
					army.sendPbMessage(message);
				}
			}
		}

		this.isSoulState = false;
		this.revivaling = false;
		this.dieTime = 0;
		HeroPollingAction heroAction = new HeroPollingAction(this);
		this.enDelayQueue(heroAction);
		return true;
	}

	public List<Buffer> getExeWayBuffers(int exeWay) {
		List<Buffer> buffers = super.getExeWayBuffers(exeWay);
		if (weaponBuffer != null && weaponBuffer.getExeWay() == exeWay) {
			buffers.add(weaponBuffer);
		}
		// for (int i = 0; i < fuseSkillBuffers.length; i++) {
		// Buffer fuseBuff = fuseSkillBuffers[i];
		// if (fuseBuff != null && fuseBuff.getExeWay() == exeWay) {
		// buffers.add(fuseBuff);
		// }
		// }
		return buffers;
	}

	public List<Buffer> getTypeBuffers(int type) {
		List<Buffer> toal = super.getTypeBuffers(type);
		if (weaponBuffer != null && weaponBuffer.getType() == type) {
			toal.add(weaponBuffer);
		}
		// for (int i = 0; i < fuseSkillBuffers.length; i++) {
		// Buffer fuseBuff = fuseSkillBuffers[i];
		// if (fuseBuff != null && fuseBuff.getType() == type) {
		// toal.add(fuseBuff);
		// }
		// }
		return toal;

	}

	/**
	 * 初始化技能
	 */
	public void readSkillInfo(HeroInfoMsg hero) {
		List<Integer> toalSkillInfos = hero.getSkillInfosList();

		if (toalSkillInfos != null && toalSkillInfos.size() != 0) {
			for (Integer tempId : toalSkillInfos) {
				SkillTempateInfo skillInfo = BattleTempMgr.getBSkillInfo(tempId);
				if (skillInfo != null) {
					// TODO 给英雄添加skillInfo自带的buffers，作为常驻buffer
				}
			}
		}

		List<Integer> battleSkilList = hero.getBattleSkillsList();

		if (battleSkilList != null && battleSkilList.size() != 0) {
			for (Integer tempId : toalSkillInfos) {
				// System.out.println("tempId: "+tempId);
				SkillTempateInfo skillInfo = BattleTempMgr.getBSkillInfo(tempId);
				if (skillInfo != null) {
					SkillActionTemplateInfo actionInfo = BattleTempMgr.getActionInfo(skillInfo.getActionId());
					if (actionInfo != null) {
						Skill skill = new Skill(actionInfo);
						skill.setSkillTempateInfo(skillInfo);
						addSkill(skill);
					}
				}
			}
		}

		List<FuseSkillMsg> fuseSkills = hero.getFuseSkillsList();
		this.setFuseSkill(null, 0);
		this.setFuseSkill(null, 1);
		this.setFuseSkill(null, 2);
		this.setFuseSkill(null, 3);
		if (fuseSkills != null && fuseSkills.size() != 0) {
			for (FuseSkillMsg fuseSkillMsg : fuseSkills) {
				if (fuseSkillMsg.getIndex() > 4) {
					Log.error("------------index is error------------" + fuseSkillMsg.getIndex());
					continue;
				}
				this.setFuseSkill(new FuseSkillVo(fuseSkillMsg.getFuseSkillId(), fuseSkillMsg.getColor()), fuseSkillMsg.getIndex() - 1);
			}
		}
	}

	/** 武器buffer */
	public void addFuseBuffer(int bufferId, int index) {
		Buffer older = this.fuseSkillBuffers[index];
		if (bufferId == 0) {
			this.fuseSkillBuffers[index] = null;
			return;
		}
		SkillBufferTemplateInfo sbinfo = BattleTempMgr.getBufferInfo(bufferId);
		if (sbinfo == null) {
			Log.error("cannot find buffer temp ,tempId : " + bufferId);
			this.fuseSkillBuffers[index] = null;
			return;
		}

		Buffer buff = BufferFactory.createBuffer(this, this, sbinfo);
		buff.setPermanent(true);
		this.fuseSkillBuffers[index] = buff;
		if (older != null) {
			older.setPermanent(false);
			older.dispose();
		}
	}

	/** 获取武器buff */
	public Buffer getFuseBuffer(int index) {
		return fuseSkillBuffers[index];
	}

	/** 魂幡buffer */
	public void addWeaponBuffer(int weaponBufId) {
		Buffer older = this.getWeaponBuffer();
		if (weaponBufId == 0) {
			this.setWeaponBuffer(null);
			return;
		}
		SkillBufferTemplateInfo sbinfo = BattleTempMgr.getBufferInfo(weaponBufId);
		if (sbinfo == null) {
			Log.error("cannot find buffer temp ,tempId : " + weaponBufId);
			this.setWeaponBuffer(null);
			return;
		}

		Buffer buff = BufferFactory.createBuffer(this, this, sbinfo);
		buff.setPermanent(true);
		this.setWeaponBuffer(buff);

		if (older != null) {
			older.setPermanent(false);
			older.dispose();
		}
	}

	@Override
	public Builder getBattlePlayerInfoMsg() {
		// TODO Auto-generated method stub
		this.cachBattleInfoPacket = BattleLivingInfoMsg.newBuilder();
		cachBattleInfoPacket.setMountState(getMountState());
		return super.getBattlePlayerInfoMsg();
	}

	// public void clearWorkBuffer() {
	// List<Buffer> allbuffer = new ArrayList<>();
	// synchronized (workBuffers) {
	// for (Entry<Integer, List<Buffer>> entry : workBuffers.entrySet()) {
	// List<Buffer> wayBufs = entry.getValue();
	// allbuffer.addAll(wayBufs);
	// wayBufs.clear();
	// }
	// workBuffers.clear();
	// }
	//
	// for (Buffer buff : allbuffer) {
	// buff.stop();
	// BufferMsg.Builder bmsg = BufferMsg.newBuilder();
	// bmsg.setBufferId(buff.getBufferId());
	// bmsg.setOption(4);// 4 删除
	// bmsg.setSourceId(buff.getSource().getId());
	// bmsg.setTargetId(buff.getTarget().getId());
	// sendBufferChange(bmsg.build());
	// }
	// }

	public void addCampaignBuff(Buffer buff) {
		campaignBuffers.add(buff);
		this.addBuffer(buff);
	}

	public void removeCampaignBuffer() {
		for (Buffer buffer : campaignBuffers) {
			removeBuffer(buffer);
		}
		campaignBuffers.clear();
	}

	private void calPKValue(Living source, Living deather) {

		// System.out.println("source playerId: " + source.toString() + "
		// source.getPkVal(): " + source.getPkVal()+"
		// source.getBattleMode():"+source.getBattleMode()+"
		// getBattleMode():"+getBattleMode());
		// 攻击源处理
		if (source.getBattleMode() == BattleModeCode.warBattleMode && getBattleMode() == BattleModeCode.peaceBattleMode) {// 增加pk值
			source.setPkVal(source.getPkVal() + 1000);
			// 通知
			Map<Integer, Long> changeMap = new HashMap<Integer, Long>();
			changeMap.put(EnumAttr.PK_VAL.getValue(), (long) source.getPkVal());
			notifyCenter(changeMap, source.getArmyId());

			List<PropertyMsg> properties = new ArrayList<>();
			PropertyMsg.Builder p = PropertyMsg.newBuilder();
			p.setBasePoint((long) source.getPkVal());
			p.setTotalPoint((long) source.getPkVal());
			p.setType(EnumAttr.PK_VAL.getValue());
			properties.add(p.build());
			updateProperty(source, properties);
		}

		// System.out.println("source playerId: " + source.getArmyId() + "
		// source.getPkVal(): " + source.getPkVal());

		// 自己
		List<PropertyMsg> properties = new ArrayList<>();
		Map<Integer, Long> changeMap = new HashMap<Integer, Long>();

		int changePkVal = 0;// 减少pk值
		if (getColour(getPkVal()) == BattleModeCode.yellow) {
			changePkVal = MathUtils.randomClamp(10, 20);
			notifyCenter(2, (int) source.getArmyId(), (int) getArmyId());
		} else if (getColour(getPkVal()) == BattleModeCode.red) {
			changePkVal = MathUtils.randomClamp(40, 80);
			notifyCenter(2, (int) source.getArmyId(), (int) getArmyId());
		}
		// System.out.println(" playerId: " + getArmyId() + " exp: " + "
		// changePkVal: " + changePkVal + " this.getPkVal(): " +
		// getPkVal());
		if (changePkVal > 0) {
			changePkVal = getPkVal() - changePkVal < 0 ? 0 : getPkVal() - changePkVal;
			setPkVal(changePkVal);
			PropertyMsg.Builder p = PropertyMsg.newBuilder();
			p.setBasePoint(changePkVal);
			p.setTotalPoint(changePkVal);
			p.setType(EnumAttr.PK_VAL.getValue());
			properties.add(p.build());
			changeMap.put(EnumAttr.PK_VAL.getValue(), (long) changePkVal);
			notifyCenter(changeMap, getArmyId());
			updateProperty(deather, properties);
		}
		// System.out.println(" ---playerId: " + getArmyId() + "
		// changePkVal: " + changePkVal + " this.getPkVal(): " +
		// getPkVal());
	}

	public boolean isRevivaling() {
		return revivaling;
	}

	public void setRevivaling(boolean revivaling) {
		this.revivaling = revivaling;
	}

	public int getMountState() {
		return mountState;
	}

	public void setMountState(int mountState) {
		this.mountState = mountState;
	}

	@Override
	public int getSpeed() {
		// TODO Auto-generated method stub
		int speed = super.getSpeed();
		if (mountState > 0) {
			if (this.simpleInfo != null && this.simpleInfo.getMountId() > 0) {
				MountGradeCfg mountCfg = MountTempleteMgr.getMountTemps().get(this.simpleInfo.getMountId());
				if (mountCfg != null) {
					speed += speed * mountCfg.getSpeed(EnumAttr.SPEED.getValue()) / 10000;
				}
			}
		}
		return speed;
	}

	public List<Integer> getMonsterRefreshIdList() {
		return monsterRefreshIdList;
	}

	public void reSetMonsterRefreshIdList(List<Integer> monsterRefreshIdList, int curCampaign) {
		ArrayList<Integer> date = new ArrayList<Integer>();
		date.addAll(monsterRefreshIdList);

		date.removeAll(this.monsterRefreshIdList);// 新增加的
		int maxId = 0; // 副本中最大刷怪点
		Campaign campaign = CampaignMgr.getCampagin(curCampaign);
		if (campaign != null) {
			Field f = campaign.getEnterField(0);
			Map<Integer, SpawnInfo> spawnInfos = SpawnTemplateMgr.getFieldSpawnInfos(f.getMapKey());
			if (date.size() > 0) {
				for (Entry<Integer, SpwanNode> entry : campaign.getSpwanNodes().entrySet()) {
					int tagId = entry.getValue().getSpawnInfo().getTagId();
					date.remove(Integer.valueOf(tagId));
					if (tagId > maxId)
						maxId = tagId;
				}
			}
			int endId = 0;
			for (int i = 0; i < date.size(); i++) {
				Integer tagId = date.get(i);
				if (tagId <= maxId)
					continue;

				// System.out.println("integerinteger: " + tagId);
				int spwanId = SpawnTemplateMgr.getSpwanId(tagId);
				SpawnInfo sf = spawnInfos.get(spwanId);
				if (sf == null)
					continue;
				if (i == date.size() - 1) {
					endId = sf.getTagId();
					sf.setCampaignFeatures(Campaign.TERMINATOR);
				}
				SpwanNode node = new BeadMonsterSpawnNode(sf, f);
				f.addSpawnNode(node);
				node.build();
				node.stateTransition(new PerareState(node));
				campaign.getSpwanNodes().put(node.getSpwanId(), node);
			}
			for (Entry<Integer, SpwanNode> entry : campaign.getSpwanNodes().entrySet()) {
				if (endId > 0 && entry.getValue().getSpawnInfo().getTagId() != endId) {
					entry.getValue().setDecorator(new CampaignNodeDecorator());
				}
			}
		}

		this.monsterRefreshIdList.clear();
		// System.out.println(" date: " + date + " monsterRefreshIdList: " +
		// monsterRefreshIdList);
		this.monsterRefreshIdList.addAll(monsterRefreshIdList);
	}

	public boolean isFlashName() {
		return flashName;
	}

	public void setFlashName(boolean flashName) {
		this.flashName = flashName;
	}

	public FuseSkillVo getFuseSkill(int bufferId) {
		return fuseSkillVos.get(bufferId);
	}

	public void setFuseSkill(FuseSkillVo fuseSkill, int index) {
		if (fuseSkill != null) {
			addFuseBuffer(fuseSkill.getSkillId(), index);
			this.fuseSkillVos.put(fuseSkill.getBufferId(), fuseSkill);
		} else {
			addFuseBuffer(0, index);
		}
	}

	public int getWeaponBuffId() {
		return weaponBuffId;
	}

	public void setWeaponBuffId(int weaponBuffId) {
		this.weaponBuffId = weaponBuffId;
	}

	public Buffer getWeaponBuffer() {
		return weaponBuffer;
	}

	public void setWeaponBuffer(Buffer weaponBuffer) {
		this.weaponBuffer = weaponBuffer;
	}

	public int getSoulLv() {
		return soulLv;
	}

	public void setSoulLv(int soulLv) {
		this.soulLv = soulLv;
	}

}
