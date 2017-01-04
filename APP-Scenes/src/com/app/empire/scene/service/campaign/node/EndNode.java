package com.app.empire.scene.service.campaign.node;

import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.state.StopState;
import com.app.empire.scene.service.campaign.state.SuccessState;
import com.app.empire.scene.service.warField.spawn.OverState;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.world.ArmyProxy;

public class EndNode extends CampaignNodeDecorator {

	public void start(Campaign campaign, SpwanNode node) {
		campaign.stateTransition(new SuccessState(campaign));
	}

	public void active(ArmyProxy army, Campaign campaign, SpwanNode node) {
		node.stateTransition(new OverState(node));
	}

	public void over(Campaign campaign, SpwanNode node) {
		campaign.stateTransition(new StopState(campaign));
	}
}
