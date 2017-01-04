package com.app.empire.scene.service.campaign.state;

import com.app.empire.scene.service.campaign.Campaign;

public class PrepareState extends CampaignState {
	
	public PrepareState(Campaign campaign) {
		super(campaign);
		this.code = CampaignState.PREPARE;
	}
	
	@Override
	public void work() {
		campaign.prepare();
	}

}
