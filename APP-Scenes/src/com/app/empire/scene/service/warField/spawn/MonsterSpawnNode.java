package com.app.empire.scene.service.warField.spawn;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.db.mysql.entity.MonsterInfo;
import com.app.db.mysql.entity.SkillActioninfo;
import com.app.db.mysql.entity.SkillBuffer;
import com.app.db.mysql.entity.SkillInfo;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.constant.SceneGlobal;
import com.app.empire.scene.constant.SpwanInfoType;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferFactory;
import com.app.empire.scene.service.battle.skill.Skill;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Monster;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.StringUtils;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.exec.DelayAction;

public class MonsterSpawnNode extends SpwanNode { // 刷怪模板
	private Logger log = Logger.getLogger(MonsterSpawnNode.class);
	protected int toalCount; // 刷怪总数
	protected int curCount; // 当前数量

	public MonsterSpawnNode(FieldSpawn info, Field field) {
		super(info, field);
		this.nodeType = SpwanInfoType.MONSTER;

	}

	public void reset() {
		this.toalCount = 0;
		this.curCount = 0;
	}

	public void stateTransition(NodeState state) {
		super.stateTransition(state);
	}

	@Override
	public void prepare() {
	}

	@Override
	public void start() {
		super.start();
		int count = spwanInfo.getMaxCount() - curCount;
		if (spwanInfo.getToalCount() > 0) {
			count = Math.max(spwanInfo.getToalCount() - toalCount, count);
		}
		count = count >= 50 ? 50 : count;
		for (int i = 0; i < count; i++) {
			curCount++;
			createChildren();
		}
	}

	public void lvingDie(Living living) {
		if (field == null) {
			return;
		}
		if (state.getCode() != NodeState.WORK) {
			return;
		}
		children.remove(living.getId());
		field.addDeathLiving(living);
		curCount = curCount - 1;
		if (isOver()) {
			stateTransition(new OverState(this));
		} else {
			if (((spwanInfo.getToalCount() == 0 || toalCount < spwanInfo.getToalCount()) && curCount < spwanInfo.getMaxCount())) {
				field.enDelayQueue(new CreateChildAction());
				// createChildren();
				curCount = curCount + 1;
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

		MonsterInfo monsterInfo = ServiceManager.getManager().getGameConfigService().getMonsterInfoTemps().get(spwanInfo.getEntityId());
		if (monsterInfo == null) {
			log.error(spwanInfo.getId() + "----" + spwanInfo.getEntityId() + " 在MonsterInfo里面未找到配置");
			return;
		}
		// 添加副本挑战任务buff
		Campaign campaign = CampaignMgr.getCampagin(campaignId);
		Monster monster = new Monster(this, monsterInfo);
		monster.setPostion(new Vector3(randomx/ Vector3.Accuracy, randomy/ Vector3.Accuracy, randomz/ Vector3.Accuracy));
		instill(monster, monsterInfo);
		children.put(monster.getId(), monster);
		field.enterField(monster);
		if (campaign != null && campaign.getTask() != null && campaign.getTask().getConditionType() == CTBaseCondition.ADD_BUFF_MONSTER) {
			String bufferIds = campaign.getTask().getTemp().getStrParam1();
			if (StringUtils.isNullOrEmpty(bufferIds)) {
				return;
			}
			String[] attr = bufferIds.split(",");
			for (String str : attr) {
				int bufferId = Integer.valueOf(str);
				SkillBuffer bufferTemp = ServiceManager.getManager().getGameConfigService().getSkillBufferTemps().get(bufferId);
				if (bufferTemp != null) {
					Buffer buffer = BufferFactory.createBuffer(monster, monster, bufferTemp);
					monster.addBuffer(buffer);
				}
			}
		}
	}

	public static MonsterInfo getDynamicMonster(MonsterInfo monsterInfo, Campaign campaign) {
		ArmyProxy creater = campaign.getCreater();
		if (creater == null) {
			return null;
		}
		int grade = creater.getPlayer().getSimpleInfo().getLevel();
		int newId = monsterInfo.getMonsterId() + grade / 10;
		return ServiceManager.getManager().getGameConfigService().getMonsterInfoTemps().get(newId);
	}

	/** 浸染 */
	public static void instill(Monster monster, MonsterInfo monsterInfo) {
		monster.setMonsterInfo(monsterInfo);
		monster.setProperty(EnumAttr.SPEED, monsterInfo.getMoveSpeed() * 100);
		// monster.setSpeed(monsterInfo.getMoveSpeed() * 100);
		monster.setSkin(monsterInfo.getMonsterId());

		monster.setProperty(EnumAttr.MAX_BLOOD, monsterInfo.getHp());
		monster.setProperty(EnumAttr.BLOOD, monsterInfo.getHp());
		monster.setProperty(EnumAttr.CUR_BLOOD, monsterInfo.getHp());

		monster.setProperty(EnumAttr.MAX_SOUL, monsterInfo.getSoulHpValue());
		monster.setProperty(EnumAttr.SOUL, monsterInfo.getSoulHpValue());
		monster.setProperty(EnumAttr.CUR_SOUL, monsterInfo.getSoulHpValue());

		monster.setProperty(EnumAttr.ATTACK, monsterInfo.getHurtValue());
		// monster.setInitAttack(monsterInfo.getHurtValue());
		monster.setProperty(EnumAttr.DEFENCE, monsterInfo.getArmorValue());
		// monster.setInitDefence(monsterInfo.getArmorValue());
		monster.setProperty(EnumAttr.SOUL_ATTACK, monsterInfo.getSoulHurtValue());
		// monster.setInitSoulAttack(monsterInfo.getSoulHurtValue());
		monster.setProperty(EnumAttr.SOUL_DEFENCE, monsterInfo.getSoulArmorValue());
		// monster.setInitSoulDefence(monsterInfo.getSoulArmorValue());

		monster.setProperty(EnumAttr.ACCURATE, monsterInfo.getHitRateValue());
		monster.setProperty(EnumAttr.DODGE, monsterInfo.getDodgeValue());
		monster.setProperty(EnumAttr.CRIT, monsterInfo.getCritValue());
		monster.setProperty(EnumAttr.CRIT_DEFENCE, monsterInfo.getToughnessValue());

		if (monsterInfo.getHp() <= 0) {
			monster.setSoulState(true);
		}
		String skillIds = monsterInfo.getSkillIds();
		if (skillIds != null && !StringUtils.isNullOrEmpty(skillIds)) {
			for (String str : skillIds.split(",")) {
				if (StringUtils.isNullOrEmpty(str)) {
					continue;
				}
				SkillInfo skillTempateInfo = ServiceManager.getManager().getGameConfigService().getSkillTemps().get(Integer.valueOf(str));
				if (skillTempateInfo == null) {
					continue;
				}
				SkillActioninfo skillActioninfo = ServiceManager.getManager().getGameConfigService().getSkillActioninfoTemps().get(skillTempateInfo.getActionId());

				if (skillActioninfo == null) {
					continue;
				}
				Skill skill = new Skill(skillActioninfo);
				skill.setSkillinfo(skillTempateInfo);
				monster.addSkill(skill);
			}
		} else {
			// 测试
			Skill test = new Skill(ServiceManager.getManager().getGameConfigService().getSkillActioninfoTemps().get(1001));
			monster.addSkill(test);
		}
		// monster.addLivingState(monsterInfo.getDefaultState());

	}

	public boolean isOver() {
		return spwanInfo.getToalCount() > 0 && toalCount >= spwanInfo.getToalCount() && children.size() == 0;
	}

	// 获取未刷出的怪物
	public int getLeftMonster() {
		int result = spwanInfo.getToalCount() - toalCount + children.size();
		if (result < 0 || result > 20) {
			log.error("node get leftMonster error,node info :" + getSpwanId());
			result = 0;
		}
		toalCount = spwanInfo.getToalCount();
		curCount = spwanInfo.getMaxCount();
		this.state = new OverState(this);
		return result;
	}

	// 获取当前所有怪物
	public List<Living> getAlives() {
		List<Living> aLive = new ArrayList<>();
		aLive.addAll(children.values());
		return aLive;
	}
}
