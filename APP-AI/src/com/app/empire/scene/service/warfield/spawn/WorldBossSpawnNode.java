package com.app.empire.scene.service.warField.spawn;

import java.util.HashSet;
import java.util.Set;

import com.chuangyou.common.util.Log;
import com.chuangyou.common.util.StringUtils;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.battle.damage.DamageStatistic;
import com.chuangyou.xianni.battle.mgr.BattleTempMgr;
import com.chuangyou.xianni.battle.skill.Skill;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignFactory;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.campaign.CampaignTempMgr;
import com.chuangyou.xianni.campaign.state.StartState;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.constant.SpwanInfoType;
import com.chuangyou.xianni.entity.campaign.CampaignTemplateInfo;
import com.chuangyou.xianni.entity.fieldBoss.FieldBossCfg;
import com.chuangyou.xianni.entity.notice.NoticeCfg;
import com.chuangyou.xianni.entity.skill.SkillTempateInfo;
import com.chuangyou.xianni.entity.spawn.MonsterInfo;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.fieldBoss.manager.WorldBossManager;
import com.chuangyou.xianni.fieldBoss.template.FieldBossTemplateMgr;
import com.chuangyou.xianni.notice.template.NoticeTemplateMgr;
import com.chuangyou.xianni.role.helper.IDMakerHelper;
import com.chuangyou.xianni.role.objects.FieldBoss;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Monster;
import com.chuangyou.xianni.role.objects.Transfer;
import com.chuangyou.xianni.role.template.MonsterInfoTemplateMgr;
import com.chuangyou.xianni.warfield.field.Field;

public class WorldBossSpawnNode extends FieldBossSpawnNode {

	public WorldBossSpawnNode(SpawnInfo info, Field field) {
		super(info, field);
		// TODO Auto-generated constructor stub
		this.nodeType = SpwanInfoType.BOSS_WORLD;
	}

	@Override
	public void prepare() {

		// 准备阶段不作处理，等待世界BOSS管理器WorldBossManager刷新BOSS
	}

	@Override
	public void over() {
		super.over();
		WorldBossManager.nodeOver();
	}

	@Override
	protected void createBoss() {
		if (state.getCode() != NodeState.WORK) {
			return;
		}
		int randomx = spwanInfo.getBound_x();
		int randomy = spwanInfo.getBound_y();
		int randomz = spwanInfo.getBound_z();

		MonsterInfo monsterInfo = MonsterInfoTemplateMgr.get(spwanInfo.getEntityId());
		if (monsterInfo == null) {
			Log.error(spwanInfo.getId() + "----" + spwanInfo.getEntityId() + " 在MonsterInfo里面未找到配置");
			return;
		}
		FieldBoss monster = new FieldBoss(this, monsterInfo);
		monster.setPostion(new Vector3(randomx / Vector3.Accuracy, randomy / Vector3.Accuracy, randomz / Vector3.Accuracy));
		instill(monster, monsterInfo);
		this.monster = monster;
		field.enterField(monster);
		System.out.println("世界BOSS：" + monster.getId() + "  mapId:" + field.getMapKey() + "  position:" + monster.getPostion());
		System.out.println("monster:" + monster + "  skinId :" + monster.getSkin());
	}

	/** 浸染(属性缩小，测试用 ) */
	public static void instill(Monster monster, MonsterInfo monsterInfo) {
		monster.setMonsterInfo(monsterInfo);
		monster.setProperty(EnumAttr.SPEED, monsterInfo.getMoveSpeed() * 100);
		// monster.setSpeed(monsterInfo.getMoveSpeed() * 100);
		monster.setSkin(monsterInfo.getMonsterId());

		monster.setProperty(EnumAttr.MAX_BLOOD, monsterInfo.getHp() / 10000);
		monster.setProperty(EnumAttr.BLOOD, monsterInfo.getHp() / 10000);
		monster.setProperty(EnumAttr.CUR_BLOOD, monsterInfo.getHp() / 10000);

		monster.setProperty(EnumAttr.MAX_SOUL, monsterInfo.getSoulHpValue() / 10000);
		monster.setProperty(EnumAttr.SOUL, monsterInfo.getSoulHpValue() / 10000);
		monster.setProperty(EnumAttr.CUR_SOUL, monsterInfo.getSoulHpValue() / 10000);

		monster.setProperty(EnumAttr.ATTACK, monsterInfo.getHurtValue() / 10000);
		// monster.setInitAttack(monsterInfo.getHurtValue());
		monster.setProperty(EnumAttr.DEFENCE, monsterInfo.getArmorValue() / 10000);
		// monster.setInitDefence(monsterInfo.getArmorValue());
		monster.setProperty(EnumAttr.SOUL_ATTACK, monsterInfo.getSoulHurtValue() / 10000);
		// monster.setInitSoulAttack(monsterInfo.getSoulHurtValue());
		monster.setProperty(EnumAttr.SOUL_DEFENCE, monsterInfo.getSoulArmorValue() / 10000);
		// monster.setInitSoulDefence(monsterInfo.getSoulArmorValue());

		monster.setProperty(EnumAttr.ACCURATE, monsterInfo.getHitRateValue() / 10000);
		monster.setProperty(EnumAttr.DODGE, monsterInfo.getDodgeValue() / 10000);
		monster.setProperty(EnumAttr.CRIT, monsterInfo.getCritValue() / 10000);
		monster.setProperty(EnumAttr.CRIT_DEFENCE, monsterInfo.getToughnessValue() / 10000);

		if (monsterInfo.getHp() <= 0) {
			monster.setSoulState(true);
		}
		String skillIds = monsterInfo.getSkillIds();
		if (skillIds != null && !StringUtils.isNullOrEmpty(skillIds)) {
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

	protected void dieTrigger(Living living) {
		FieldBossCfg bossCfg = FieldBossTemplateMgr.getWorldBossCfg();
		if (bossCfg == null)
			return;

		// 创建副本
		CampaignTemplateInfo tempInfo = CampaignTempMgr.get(bossCfg.getOpenCampaignId());
		if (tempInfo == null) {
			Log.error("世界BOSS死亡触发副本配置错误：bossMonsterId = " + bossCfg.getMonsterId() + "  campaignId = " + bossCfg.getOpenCampaignId());
			return;
		}

		// 创建传送门
		Transfer transfer = new Transfer(IDMakerHelper.nextID(), Transfer.CAMPAIGN_TRANSFER);
		transfer.setPostion(living.getPostion());
		transfer.setSkin(bossCfg.getTransferNpcId());
		FieldBoss boss = (FieldBoss) living;
		if (boss != null) {
			DamageStatistic statistic = boss.getStatistic();
			if (statistic != null) {
				Set<Long> canEnterIds = new HashSet<>();
				canEnterIds.addAll(statistic.getDamageMap().keySet());
				transfer.setCanEnterIds(canEnterIds);
			}
		}

		// 创建副本
		Campaign campaign = CampaignFactory.createWorldBossTriggerCampaign(tempInfo, transfer);
		CampaignMgr.add(campaign);
		campaign.stateTransition(new StartState(campaign));

		transfer.setTargetId(campaign.getIndexId());
		field.enterField(transfer);

		NoticeCfg noticeCfg = NoticeTemplateMgr.getNoticeCfg(bossCfg.getDeadNotice());
		dieNotice(noticeCfg, noticeCfg.getContent());
	}

	@Override
	public FieldBossCfg getBossCfg() {
		// TODO Auto-generated method stub
		FieldBossCfg bossCfg = FieldBossTemplateMgr.getWorldBossCfg();
		return bossCfg;
	}

}
