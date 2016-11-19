package com.app.empire.scene.service.campaign.node;

import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.warfield.spawn.OverState;
import com.chuangyou.xianni.warfield.spawn.SpwanNode;
import com.chuangyou.xianni.world.ArmyProxy;

public class EndNode extends CampaignNodeDecorator {

	public void start(Campaign campaign, SpwanNode node) {
		campaign.passCampaign();
	}

	public void active(ArmyProxy army, Campaign campaign, SpwanNode node) {
		node.stateTransition(new OverState(node));
	}

	public void over(Campaign campaign, SpwanNode node) {
		campaign.over();
	}
}
