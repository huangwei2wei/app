package com.app.empire.scene.service.campaign.node;

import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.warfield.spawn.SpwanNode;

public class BornNode extends CampaignNodeDecorator {

	public void start(Campaign campaign, SpwanNode node) {
		campaign.changeBornNode(node);
	}
}
