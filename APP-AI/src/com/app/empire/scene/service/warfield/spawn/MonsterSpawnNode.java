package com.app.empire.scene.service.warfield.spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.app.empire.scene.entity.FieldSpawn;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.exec.DelayAction;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferFactory;
import com.app.empire.scene.service.battle.mgr.BattleTempMgr;
import com.app.empire.scene.service.battle.skill.Skill;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.constant.SceneGlobal;
import com.app.empire.scene.constant.SpwanInfoType;
import com.app.empire.scene.entity.SkillBuffer;
import com.app.empire.scene.entity.Skillinfo;
import com.app.empire.scene.entity.MonsterInfo;
import com.app.empire.scene.entity.FieldSpawn;
//import com.chuangyou.xianni.exec.DelayAction;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Monster;
//import com.chuangyou.xianni.role.template.MonsterInfoTemplateMgr;
import com.app.empire.scene.service.warfield.field.Field;

public class MonsterSpawnNode extends SpwanNode { // 刷怪模板
	protected int toalCount; // 刷怪总数
	protected int curCount; // 当前数量
	protected Map<Long, Living> children; // 子孙们

	public MonsterSpawnNode(FieldSpawn info, Field field) {
		super(info, field);
		this.nodeType = SpwanInfoType.MONSTER;
		this.children = new ConcurrentHashMap<>();
	}

	public void reset() {
		this.toalCount = 0;
		this.curCount = 0;
		this.children.clear();
	}

	public void stateTransition(NodeState state) {
		super.stateTransition(state);
	}

	@Override
	public void prepare() {
		System.out.println(".........准备状态...........");
	}

	@Override
	public void start() {
		super.start();
		System.err.println("spwanInfo :" + spwanInfo.getTagId());
		if (!(curCount < spwanInfo.getMaxCount() && (toalCount < spwanInfo.getToalCount() || spwanInfo.getToalCount() <= 0))) {
			System.out.println("--------------------------------curCount: " + curCount + " spwanInfo :" + spwanInfo.getTagId());
		}

		while (curCount < spwanInfo.getMaxCount() && (toalCount < spwanInfo.getToalCount() || spwanInfo.getToalCount() <= 0)) {
			System.out.println("---------curCount: " + curCount + " spwanInfo :" + spwanInfo.getTagId());
			curCount++;
			createChildren();
		}
	}

	public void lvingDie(Living living) {
		if (field != null) {
			children.remove(living.getId());
			field.addDeathLiving(living);
			curCount--;
			if (isOver()) {
				stateTransition(new OverState(this));
			} else {
				if (((spwanInfo.getToalCount() == 0 || toalCount < spwanInfo.getToalCount()) && curCount < spwanInfo.getMaxCount())) {
					field.enDelayQueue(new CreateChildAction());
					curCount++;
					System.out.println("---------&&&curCount: " + curCount + " spwanInfo :" + spwanInfo.getTagId());
				}
			}
		}
	}

	class CreateChildAction extends DelayAction {
		public CreateChildAction() {
			super(field, SceneGlobal.MONSTER_SPAWN_TIME);
		}

		@Override
		public void execute() {
			createChildren();
		}
	}

	protected void createChildren() {
		if (isOver() || (state != null && state.code >= NodeState.OVER)) {
			return;
		}
		toalCount++;

		int randomx = spwanInfo.getBoundX();
		int randomy = spwanInfo.getBoundY();
		int randomz = spwanInfo.getBoundZ();

		Monster monster = new Monster(this);
		MonsterInfo monsterInfo = MonsterInfoTemplateMgr.get(spwanInfo.getEntityId());
		if (monsterInfo != null) {
			monster.setPostion(new Vector3(randomx / Vector3.Accuracy, randomy / Vector3.Accuracy, randomz / Vector3.Accuracy));
			instill(monster, monsterInfo);
			children.put(monster.getId(), monster);
			field.enterField(monster);
		} else {
			Log.error(spwanInfo.getId() + "----" + spwanInfo.getEntityId() + " 在MonsterInfo里面未找到配置");
		}

		// 添加副本挑战任务buff
		Campaign campaign = CampaignMgr.getCampagin(campaignId);
		if (campaign != null && campaign.getTask() != null && campaign.getTask().getConditionType() == CTBaseCondition.ADD_BUFF_MONSTER) {
			String bufferIds = campaign.getTask().getTemp().getStrParam1();
			if (bufferIds != null && !bufferIds.equals("")) {
				String[] attr = bufferIds.split(",");
				for (String str : attr) {
					int bufferId = Integer.valueOf(str);
					SkillBufferTemplateInfo bufferTemp = BattleTempMgr.getBufferInfo(bufferId);
					if (bufferTemp != null) {
						Buffer buffer = BufferFactory.createBuffer(monster, monster, bufferTemp);
						monster.addBuffer(buffer);
					}
				}
			}

		}
	}

	/** 浸染 */
	public static void instill(Monster monster, MonsterInfo monsterInfo) {
		monster.setMonsterInfo(monsterInfo);
		monster.setSpeed(monsterInfo.getMoveSpeed() * 100);
		monster.setSkin(monsterInfo.getMonsterId());

		monster.setProperty(EnumAttr.MAX_BLOOD, monsterInfo.getHp());
		monster.setProperty(EnumAttr.BLOOD, monsterInfo.getHp());
		monster.setProperty(EnumAttr.CUR_BLOOD, monsterInfo.getHp());

		monster.setProperty(EnumAttr.MAX_SOUL, monsterInfo.getSoulHpValue());
		monster.setProperty(EnumAttr.SOUL, monsterInfo.getSoulHpValue());
		monster.setProperty(EnumAttr.CUR_SOUL, monsterInfo.getSoulHpValue());

		monster.setProperty(EnumAttr.ATTACK, monsterInfo.getHurtValue());
		monster.setInitAttack(monsterInfo.getHurtValue());
		monster.setProperty(EnumAttr.DEFENCE, monsterInfo.getArmorValue());
		monster.setInitDefence(monsterInfo.getArmorValue());
		monster.setProperty(EnumAttr.SOUL_ATTACK, monsterInfo.getSoulHurtValue());
		monster.setInitSoulAttack(monsterInfo.getSoulHurtValue());
		monster.setProperty(EnumAttr.SOUL_DEFENCE, monsterInfo.getSoulArmorValue());
		monster.setInitSoulDefence(monsterInfo.getSoulArmorValue());

		monster.setProperty(EnumAttr.ACCURATE, monsterInfo.getHitRateValue());
		monster.setProperty(EnumAttr.DODGE, monsterInfo.getDodgeValue());
		monster.setProperty(EnumAttr.CRIT, monsterInfo.getCritValue());
		monster.setProperty(EnumAttr.CRIT_DEFENCE, monsterInfo.getToughnessValue());

		if (monsterInfo.getHp() <= 0) {
			monster.setSoulState(true);
		}
		String skillIds = monsterInfo.getSkillIds();
		if (skillIds != null && StringUtils.isNullOrEmpty(skillIds)) {
			for (String str : skillIds.split(",")) {
				if (StringUtils.isNullOrEmpty(str)) {
					continue;
				}
				SkillTempateInfo skillTempateInfo = BattleTempMgr.getBSkillInfo(Integer.valueOf(str));
				if (skillTempateInfo == null) {
					continue;
				}
				if (BattleTempMgr.getActionInfo(skillTempateInfo.getActionId()) == null) {
					continue;
				}
				Skill skill = new Skill(BattleTempMgr.getActionInfo(skillTempateInfo.getActionId()));
				skill.setSkillTempateInfo(skillTempateInfo);
				monster.addSkill(skill);
			}
		} else {
			// 测试
			Skill test = new Skill(BattleTempMgr.getActionInfo(1001));
			monster.addSkill(test);
		}

	}

	public boolean isOver() {
		return spwanInfo.getToalCount() > 0 && toalCount >= spwanInfo.getToalCount() && children.size() == 0;
	}

	// 获取未刷出的怪物
	public int getLeftMonster() {
		int result = spwanInfo.getToalCount() - toalCount + children.size();
		if (result < 0 || result > 20) {
			Log.error("node get leftMonster error,node info :" + getSpwanId());
			result = 0;
		}
		toalCount = spwanInfo.getToalCount();
		curCount = spwanInfo.getMaxCount();
		System.out.println("==---------curCount: " + curCount + " spwanInfo :" + spwanInfo.getTagId());
		this.state = new OverState(this);
		return result;
	}

	// 获取当前所有怪物
	public List<Living> getAlive() {
		List<Living> aLive = new ArrayList<>();
		aLive.addAll(children.values());
		return aLive;
	}
}
