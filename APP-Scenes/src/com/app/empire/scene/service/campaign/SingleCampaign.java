package com.app.empire.scene.service.campaign;

import com.app.db.mysql.entity.CampaignInfo;
import com.app.empire.scene.service.world.ArmyProxy;

public class SingleCampaign extends Campaign {

	public SingleCampaign(CampaignInfo tempInfo, ArmyProxy creater, int taskId) {
		super(tempInfo, creater, taskId);
	}

	public boolean agreedToEnter(ArmyProxy army) {
		if (super.agreedToEnter(army)) {
			return army.getPlayerId() == creater;
		}
		return false;
	}
}
