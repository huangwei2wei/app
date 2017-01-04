package com.app.empire.scene.service.campaign.state;

import com.app.empire.scene.service.campaign.Campaign;

public class StartState extends CampaignState {

	public StartState(Campaign campaign) {
		super(campaign);
		this.code = CampaignState.START;
	}

	@Override
	public void work() {
		campaign.start();
	}

}
