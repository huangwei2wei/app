package com.app.empire.scene.service.campaign.state;

import com.chuangyou.xianni.campaign.Campaign;

public class PrepareState extends CampaignState {
	
	public PrepareState(Campaign campaign) {
		super(campaign);
		this.code = CampaignState.PREPARE;
	}
	
	@Override
	public void work() {

	}

}
