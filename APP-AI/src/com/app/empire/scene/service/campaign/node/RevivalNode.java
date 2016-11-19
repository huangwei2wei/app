package com.app.empire.scene.service.campaign.node;

import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.warfield.spawn.SpwanNode;

public class RevivalNode extends CampaignNodeDecorator {

	public void start(Campaign campaign, SpwanNode node) {
		campaign.changeRevivalNode(node);
	}

}
