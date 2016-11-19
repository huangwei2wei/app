package com.app.empire.scene.service.campaign;

import com.chuangyou.xianni.entity.campaign.CampaignTemplateInfo;
import com.chuangyou.xianni.world.ArmyProxy;

public class SingleCampaign extends Campaign {

	public SingleCampaign(CampaignTemplateInfo tempInfo, ArmyProxy creater, int taskId) {
		super(tempInfo, creater, taskId);
	}

	public boolean agreedToEnter(ArmyProxy army) {
		if (super.agreedToEnter(army)) {
			return army.getPlayerId() == creater;
		}
		return false;
	}
}
