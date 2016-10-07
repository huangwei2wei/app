package com.app.empire.scene.service.campaign.task.condition;

import com.chuangyou.xianni.campaign.task.CTBaseCondition;
import com.chuangyou.xianni.entity.campaign.CampaignTaskTemplateInfo;

/** 击杀N种怪物 */
public class KillerMonsterKindsCondition extends CTBaseCondition {

	public KillerMonsterKindsCondition(CampaignTaskTemplateInfo tempInfo) {
		super(tempInfo);
	}

	public boolean addProgress(int param) {
		if (tempInfo.getStrParam1().indexOf(String.valueOf(param)) >= 0 && !record.getAttrParams().contains(param) && !isComplated()) {
			record.setProgress(record.getProgress() + 1);
			record.getAttrParams().add(param);
			return true;
		}
		return false;
	}

	/** 是否完成 */
	public boolean isComplated() {
		String[] areas = tempInfo.getStrParam1().split(",");
		return record.getProgress() >= areas.length;
	}

}
