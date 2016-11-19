package com.app.empire.scene.service.campaign.node;

import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.warfield.spawn.SpwanNode;

public class GroupCreaterNode extends CampaignNodeDecorator{
	

	public void build(Campaign campaign, SpwanNode node) {
		if (campaign != null) {
			campaign.addTeamNode(node);
		}

	}
}
