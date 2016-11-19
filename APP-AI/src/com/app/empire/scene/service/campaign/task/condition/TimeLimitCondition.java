package com.app.empire.scene.service.campaign.task.condition;

import com.chuangyou.xianni.campaign.task.CTBaseCondition;
import com.chuangyou.xianni.entity.campaign.CampaignTaskTemplateInfo;

/**规定时间内通关副本*/
public class TimeLimitCondition extends CTBaseCondition {

	public TimeLimitCondition(CampaignTaskTemplateInfo tempInfo) {
		super(tempInfo);
		record.setLongParam1(System.currentTimeMillis() + tempInfo.getParam1() * 1000);

	}

	/** 是否完成 */
	public boolean isComplated() {
		return System.currentTimeMillis() <= record.getLongParam1();
	}

}
