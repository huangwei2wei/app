package com.app.empire.scene.service.campaign.task.condition;

import com.app.db.mysql.entity.CampaignTaskInfo;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;

public class GetIemCountCondition extends CTBaseCondition {

	public GetIemCountCondition(CampaignTaskInfo tempInfo) {
		super(tempInfo);
	}

	/** 是否完成 */
	public boolean isComplated() {
		return record.getProgress() >= tempInfo.getParam2();
	}

	public boolean addProgress(int param) {
		if (tempInfo.getParam1() == param) {
			record.setProgress(record.getProgress() + 1);
			return true;
		}
		return false;
	}

}
