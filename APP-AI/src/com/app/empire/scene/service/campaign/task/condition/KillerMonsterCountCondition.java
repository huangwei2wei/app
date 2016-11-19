package com.app.empire.scene.service.campaign.task.condition;

import com.chuangyou.xianni.campaign.task.CTBaseCondition;
import com.chuangyou.xianni.entity.campaign.CampaignTaskTemplateInfo;

/** 击杀某种怪物达到一定次数 */
public class KillerMonsterCountCondition extends CTBaseCondition {

	public KillerMonsterCountCondition(CampaignTaskTemplateInfo tempInfo) {
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
