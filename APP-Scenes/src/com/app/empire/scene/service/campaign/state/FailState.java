package com.app.empire.scene.service.campaign.state;

import com.app.empire.scene.service.campaign.Campaign;

public class FailState extends CampaignState {

	public FailState(Campaign campaign) {
		super(campaign);
		this.code = CampaignState.FAIL;
	}

	@Override
	public void work() {
		campaign.fail();
	}
}
