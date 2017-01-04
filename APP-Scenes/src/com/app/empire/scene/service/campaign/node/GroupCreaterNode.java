package com.app.empire.scene.service.campaign.node;

import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.warField.spawn.SpwanNode;

public class GroupCreaterNode extends CampaignNodeDecorator{
	

	public void build(Campaign campaign, SpwanNode node) {
		if (campaign != null) {
			campaign.addTeamNode(node);
		}

	}
}
