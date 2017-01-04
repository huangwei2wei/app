package com.app.empire.scene.service.campaign.task;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.CampaignTaskInfo;
import com.app.empire.scene.service.campaign.task.condition.BeKillLimitCondition;
import com.app.empire.scene.service.campaign.task.condition.KillerMonsterCountCondition;
import com.app.empire.scene.service.campaign.task.condition.KillerMonsterKindsCondition;
import com.app.empire.scene.service.campaign.task.condition.TimeLimitCondition;
import com.app.empire.scene.service.campaign.task.condition.TouchAreaCondition;
import com.app.empire.scene.service.role.objects.Living;

public class CTBaseCondition {
	protected static Logger log = Logger.getLogger(Living.class);

	protected CampaignTaskInfo tempInfo;
	protected CTRecord record; // 记录

	public CTBaseCondition(CampaignTaskInfo tempInfo) {
		this.tempInfo = tempInfo;
		this.record = new CTRecord();
	}

	public boolean addProgress(int param) {
		return true;
	}

	/** 是否完成 */
	public boolean isComplated() {
		return true;
	}

	public CampaignTaskInfo getTempInfo() {
		return tempInfo;
	}

	public CTRecord getRecord() {
		return record;
	}

	public static CTBaseCondition createCondition(CampaignTaskInfo tempInfo) {
		switch (tempInfo.getConditionType()) {
		case ADD_BUFF_MONSTER:
			return new CTBaseCondition(tempInfo);
		case ADD_BUFF_PLAYER:
			return new CTBaseCondition(tempInfo);
		case PASS_TIME_LIMIT:
			return new TimeLimitCondition(tempInfo);
		case LESS_DEAD_COUNT:
			return new BeKillLimitCondition(tempInfo);
		case TUCH_ARI:
			return new TouchAreaCondition(tempInfo);
		case KILL_MONSTER_COUNT:
			return new KillerMonsterCountCondition(tempInfo);
		case KILL_MONSTER_KIND:
			return new KillerMonsterKindsCondition(tempInfo);
		case CREATE_SPECIES_MONSTER:
			return new CTBaseCondition(tempInfo);
		case GET_ITEM_COUNT:
			return new CTBaseCondition(tempInfo);
		case COMPLATED_QTE:
			return new CTBaseCondition(tempInfo);
		default:
			log.error("createCondition error ,tempInfoId :" + tempInfo.getId() + "  ctype :" + tempInfo.getConditionType());
			return new CTBaseCondition(tempInfo);
		}
	}

	/** 给怪物添加buff */
	public static final int ADD_BUFF_MONSTER = 1;
	/** 给所有玩家增加buff */
	public static final int ADD_BUFF_PLAYER = 2;
	/** 配置的时限内通关副本 */
	public static final int PASS_TIME_LIMIT = 3;
	/** 小于等于配置的死亡次数（组队本计算全员死亡次数） */
	public static final int LESS_DEAD_COUNT = 4;
	/** 达到指定区域（可配置多个区域） */
	public static final int TUCH_ARI = 5;
	/** 击杀指定怪物一定次数 */
	public static final int KILL_MONSTER_COUNT = 6;
	/** 击杀特殊怪物（可配置多种、每种只计算一次） */
	public static final int KILL_MONSTER_KIND = 7;
	/** 刷新特殊怪物 */
	public static final int CREATE_SPECIES_MONSTER = 8;
	/** 获得指定道具 */
	public static final int GET_ITEM_COUNT = 9;
	/** 完成指定QTE操作 */
	public static final int COMPLATED_QTE = 10;
}
