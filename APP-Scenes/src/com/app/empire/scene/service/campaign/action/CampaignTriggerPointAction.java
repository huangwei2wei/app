package com.app.empire.scene.service.campaign.action;

import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.warField.spawn.SpwanNode;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.thread.exec.Action;

public class CampaignTriggerPointAction extends Action {
	private Campaign campaign;
	private ArmyProxy army;
	private SpwanNode node;

	public CampaignTriggerPointAction(Campaign campaign, ArmyProxy army, SpwanNode node) {
		super(campaign);
		// TODO Auto-generated constructor stub
		this.campaign = campaign;
		this.army = army;
		this.node = node;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

		campaign.triggerPoint(army, node);
	}

}
