package com.app.empire.scene.service.campaign;

import com.app.db.mysql.entity.CampaignInfo;
import com.app.empire.scene.service.world.ArmyProxy;

public class ChallengeCampaign extends Campaign {

	public ChallengeCampaign(CampaignInfo tempInfo, ArmyProxy creater, int taskId) {
		super(tempInfo, creater, taskId);
	}

}
