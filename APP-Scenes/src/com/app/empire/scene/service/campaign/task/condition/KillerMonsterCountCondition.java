package com.app.empire.scene.service.campaign.task.condition;

import com.app.db.mysql.entity.CampaignTaskInfo;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;

/** 击杀某种怪物达到一定次数 */
public class KillerMonsterCountCondition extends CTBaseCondition {

	public KillerMonsterCountCondition(CampaignTaskInfo tempInfo) {
		super(tempInfo);
	}

	/** 是否完成 */
	public boolean isComplated() {
		return record.getProgress() >= tempInfo.getParam2();
	}

	public boolean addProgress(int param) {
		if (tempInfo.getParam1() == param && !isComplated()) {
			record.setProgress(record.getProgress() + 1);
			return true;
		}
		return false;
	}

}
