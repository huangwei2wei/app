package com.app.empire.scene.service.campaign.state;

import com.app.empire.scene.service.campaign.Campaign;

public class SuccessState extends CampaignState {

	public SuccessState(Campaign campaign) {
		super(campaign);
		this.code = CampaignState.SUCCESS;
	}

	@Override
	public void work() {
		campaign.success();
	}
}
