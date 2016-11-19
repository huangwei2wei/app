
package com.app.empire.scene.service.warField.spawn;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import com.chuangyou.common.util.Log;
import com.chuangyou.common.util.StringUtils;
import com.chuangyou.common.util.ThreadSafeRandom;
import com.chuangyou.common.util.TimeUtil;
import com.chuangyou.common.util.Vector3;
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
import com.chuangyou.xianni.entity.fieldBoss.FieldBossDieInfo;
import com.chuangyou.xianni.entity.notice.NoticeCfg;
import com.chuangyou.xianni.entity.skill.SkillTempateInfo;
import com.chuangyou.xianni.entity.spawn.MonsterInfo;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.fieldBoss.action.CreateEliteBossAction;
import com.chuangyou.xianni.fieldBoss.manager.FieldBossHelper;
import com.chuangyou.xianni.fieldBoss.template.FieldBossTemplateMgr;
import com.chuangyou.xianni.notice.template.NoticeTemplateMgr;
import com.chuangyou.xianni.role.helper.IDMakerHelper;
import com.chuangyou.xianni.role.objects.FieldBoss;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Monster;
import com.chuangyou.xianni.role.objects.Transfer;
import com.chuangyou.xianni.role.template.MonsterInfoTemplateMgr;
import com.chuangyou.xianni.sql.dao.DBManager;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

public class EliteBossSpawnNode extends FieldBossSpawnNode {

	private FieldBossCfg bossCfg;

	public EliteBossSpawnNode(SpawnInfo spawnInfo, Field field) {
		super(spawnInfo, field);
		// TODO Auto-generated constructor stub
		this.nodeType = SpwanInfoType.BOSS_ELITE;
		bossCfg = FieldBossTemplateMgr.getFieldBossMap().get(spawnInfo.getEntityId());
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

		if (state.getCode() != NodeState.PREPARE)
			return;

		// 初始化时从库里查询BOSS死亡时间
		if (this.bossCfg == null) {
			Log.error("精英BOSS配置错误: " + spwanInfo.getId() + "-------" + spwanInfo.getEntityId() + "在FieldBoss中未找到配置");
			return;
		}
		FieldBossDieInfo info = DBManager.getFieldBossInfoDao().get(bossCfg.getMonsterId());
		if (info != null) {
			// 启服务器时，如果已经保存了下次刷新时间，按保存的时间刷新
			long nextTime = info.getNextTime().getTime();
			long currentTimeMillis = System.currentTimeMillis();

			int delay = 0;
			if (nextTime <= currentTimeMillis) {
				delay = 1;
			} else {
				delay = (int) (nextTime - currentTimeMillis);
				if (delay < 0) {
					Log.error("精英BOSS刷新时间计算错误: " + spwanInfo.getId() + "-----" + spwanInfo.getEntityId());
					return;
				}
			}
			field.enDelayQueue(new CreateEliteBossAction(field, delay, this));
		} else {
			this.computeNextNode();
		}
	}

	@Override
	public void over() {
		// TODO Auto-generated method stub
		super.over();
		computeNextNode();
	}

	/**
	 * 计算并执行下一个节点刷新时间
	 */
	private void computeNextNode() {
		if (this.bossCfg == null) {
			Log.error("精英BOSS配置错误: " + spwanInfo.getId() + "-------" + spwanInfo.getEntityId() + "在FieldBoss中未找到配置");
			return;
		}

		List<String> timeList = bossCfg.getTimeList();
		Calendar nextTimeCalendar = null;
		long currentTimeMillis = System.currentTimeMillis();

		boolean isToday = false;
		for (String timeCode : timeList) {

			nextTimeCalendar = TimeUtil.getCalendar(TimeUtil.getDateFromNowByString(timeCode));

			if (currentTimeMillis < nextTimeCalendar.getTimeInMillis()) {
				isToday = true;
				break;
			}
		}

		if (!isToday) {
			String timeCode = timeList.get(0);

			nextTimeCalendar = TimeUtil.getCalendar(TimeUtil.getDateFromNowByString(timeCode));
			nextTimeCalendar.add(Calendar.DATE, 1);
		}

		long nextTime = nextTimeCalendar.getTimeInMillis();

		int delay = (int) (nextTime - currentTimeMillis);
		if (delay < 0) {
			Log.error("精英BOSS刷新时间计算错误: " + spwanInfo.getId() + "-----" + spwanInfo.getEntityId());
			return;
		}
		field.enDelayQueue(new CreateEliteBossAction(field, delay, this));

		FieldBossHelper.bossNextTimeUpdate(bossCfg.getMonsterId(), currentTimeMillis, nextTime);
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

		System.out.println("monster:" + monster + "  skinId :" + monster.getSkin());
	}

	/** 浸染(属性缩小，测试用 ) */
	public static void instill(Monster monster, MonsterInfo monsterInfo) {
		monster.setMonsterInfo(monsterInfo);
		monster.setProperty(EnumAttr.SPEED, monsterInfo.getMoveSpeed() * 100);
		// monster.setSpeed(monsterInfo.getMoveSpeed() * 100);
		monster.setSkin(monsterInfo.getMonsterId());

		monster.setProperty(EnumAttr.MAX_BLOOD, monsterInfo.getHp() / 100);
		monster.setProperty(EnumAttr.BLOOD, monsterInfo.getHp() / 100);
		monster.setProperty(EnumAttr.CUR_BLOOD, monsterInfo.getHp() / 100);

		monster.setProperty(EnumAttr.MAX_SOUL, monsterInfo.getSoulHpValue() / 100);
		monster.setProperty(EnumAttr.SOUL, monsterInfo.getSoulHpValue() / 100);
		monster.setProperty(EnumAttr.CUR_SOUL, monsterInfo.getSoulHpValue() / 100);

		monster.setProperty(EnumAttr.ATTACK, monsterInfo.getHurtValue() / 100);
		// monster.setInitAttack(monsterInfo.getHurtValue());
		monster.setProperty(EnumAttr.DEFENCE, monsterInfo.getArmorValue() / 100);
		// monster.setInitDefence(monsterInfo.getArmorValue());
		monster.setProperty(EnumAttr.SOUL_ATTACK, monsterInfo.getSoulHurtValue() / 100);
		// monster.setInitSoulAttack(monsterInfo.getSoulHurtValue());
		monster.setProperty(EnumAttr.SOUL_DEFENCE, monsterInfo.getSoulArmorValue() / 100);
		// monster.setInitSoulDefence(monsterInfo.getSoulArmorValue());

		monster.setProperty(EnumAttr.ACCURATE, monsterInfo.getHitRateValue() / 100);
		monster.setProperty(EnumAttr.DODGE, monsterInfo.getDodgeValue() / 100);
		monster.setProperty(EnumAttr.CRIT, monsterInfo.getCritValue() / 100);
		monster.setProperty(EnumAttr.CRIT_DEFENCE, monsterInfo.getToughnessValue() / 100);

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
		if (this.bossCfg == null)
			return;
		// 是否触发事件
		ThreadSafeRandom random = new ThreadSafeRandom();
		boolean openEvent = random.isSuccessful(bossCfg.getCampaignChance(), 10000);

		NoticeCfg noticeCfg = null;
		String noticeContent = "";
		if (openEvent == true) {
			int openEventType = random.next(1, 2);

			// 创建副本
			CampaignTemplateInfo tempInfo = CampaignTempMgr.get(bossCfg.getOpenCampaignId());
			if (tempInfo == null) {
				Log.error("精英BOSS死亡触发副本配置错误：bossMonsterId = " + bossCfg.getMonsterId() + "  campaignId = " + bossCfg.getOpenCampaignId());
				return;
			}

			if (openEventType == 1) { // 多人副本
				// 创建传送门
				Transfer transfer = new Transfer(IDMakerHelper.nextID(), Transfer.CAMPAIGN_TRANSFER);
				transfer.setPostion(living.getPostion());
				transfer.setSkin(bossCfg.getTransferNpcId());
				transfer.setMinLevel(bossCfg.getMinLevel());
				transfer.setMaxLevel(bossCfg.getMaxLevel());
				field.enterField(transfer);

				// 创建副本
				Campaign campaign = CampaignFactory.createEliteTriggerCampaign(tempInfo, transfer, bossCfg.getMultiTag());
				CampaignMgr.add(campaign);
				campaign.stateTransition(new StartState(campaign));

				transfer.setTargetId(campaign.getIndexId());

				noticeCfg = NoticeTemplateMgr.getNoticeCfg(bossCfg.getMultiNotice());
				noticeContent = noticeCfg.getContent();
				noticeContent = noticeContent.replaceAll("@minLev@", String.valueOf(bossCfg.getMinLevel()));
				noticeContent = noticeContent.replaceAll("@maxLev@", String.valueOf(bossCfg.getMaxLevel()));
			} else {
				// 周围随机一个玩家进入副本
				Set<Long> playerSet = living.getNears(new PlayerSelectorHelper(living));
				if (playerSet.size() <= 0) {
					return;
				}
				Long[] players = new Long[playerSet.size()];
				int randomIndex = (new ThreadSafeRandom()).next(playerSet.size());
				playerSet.toArray(players);
				long enterPlayerId = players[randomIndex];

				Campaign campaign = CampaignFactory.createEliteTriggerCampaign(tempInfo, null, bossCfg.getSingleTag());
				CampaignMgr.add(campaign);
				campaign.stateTransition(new StartState(campaign));

				ArmyProxy enterArmy = WorldMgr.getArmy(enterPlayerId);
				campaign.onPlayerEnter(enterArmy);

				noticeCfg = NoticeTemplateMgr.getNoticeCfg(bossCfg.getSingleNotice());
				noticeContent = noticeCfg.getContent();
				noticeContent = noticeContent.replaceAll("@enterName@", enterArmy.getPlayer().getSimpleInfo().getNickName());
			}
		} else {
			noticeCfg = NoticeTemplateMgr.getNoticeCfg(bossCfg.getDeadNotice());
			noticeContent = noticeCfg.getContent();
		}
		dieNotice(noticeCfg, noticeContent);
	}

	@Override
	public FieldBossCfg getBossCfg() {
		// TODO Auto-generated method stub
		return bossCfg;
	}

}
