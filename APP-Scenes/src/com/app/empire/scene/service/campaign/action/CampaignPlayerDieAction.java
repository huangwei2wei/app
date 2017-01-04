package com.app.empire.scene.service.campaign.action;

import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Player;
import com.app.empire.scene.util.exec.Action;

public class CampaignPlayerDieAction extends Action {

	private Campaign campaign;
	private Player player;
	private Living source;
	
	public CampaignPlayerDieAction(Campaign campaign, Player player, Living source) {
		super(campaign);
		// TODO Auto-generated constructor stub
		this.campaign = campaign;
		this.player = player;
		this.source = source;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		campaign.playerDie(player, source);
	}

}
