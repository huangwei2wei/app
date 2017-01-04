package com.app.empire.scene.service.campaign.task.condition;

import com.app.db.mysql.entity.CampaignTaskInfo;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;

/** 成员最小死亡次数 */
public class BeKillLimitCondition extends CTBaseCondition {

	public BeKillLimitCondition(CampaignTaskInfo tempInfo) {
		super(tempInfo);
	}

	/** 是否完成 */
	public boolean isComplated() {
		return record.getProgress() > tempInfo.getParam1();
	}

	public boolean addProgress(int param) {
		if (!isComplated()) {
			record.setProgress(record.getProgress() + param);
			return true;
		}
		return false;
	}
}
