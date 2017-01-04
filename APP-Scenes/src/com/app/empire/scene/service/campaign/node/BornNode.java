package com.app.empire.scene.service.campaign.node;

import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.warField.spawn.SpwanNode;

public class BornNode extends CampaignNodeDecorator {

	public void start(Campaign campaign, SpwanNode node) {
		campaign.changeBornNode(node);
	}
}
