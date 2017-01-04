package com.app.empire.scene.service.campaign.task.condition;

import com.app.db.mysql.entity.CampaignTaskInfo;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;

/**规定时间内通关副本*/
public class TimeLimitCondition extends CTBaseCondition {

	public TimeLimitCondition(CampaignTaskInfo tempInfo) {
		super(tempInfo);
		record.setLongParam1(System.currentTimeMillis() + tempInfo.getParam1() * 1000);

	}

	/** 是否完成 */
	public boolean isComplated() {
		return System.currentTimeMillis() <= record.getLongParam1();
	}

}
