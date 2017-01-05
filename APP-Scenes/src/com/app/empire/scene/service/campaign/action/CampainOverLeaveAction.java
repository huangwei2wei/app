package com.app.empire.scene.service.campaign.action;

import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.warField.action.EnterFieldAction;
import com.app.empire.scene.service.world.ArmyPositionRecord;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.thread.exec.Action;

public class CampainOverLeaveAction extends Action {
	Campaign campaign;
	ArmyProxy army;

	public CampainOverLeaveAction(Campaign campaign, ArmyProxy army) {
		super(campaign);
		this.army = army;
		this.campaign = campaign;
	}

	@Override
	public void execute() {
		campaign.removeArmy(army, true);
		army.getPlayer().removeCampaignBuffer();
		ArmyPositionRecord posRecord = army.getPosRecord();
		if (posRecord == null) {
			army.returnBornMap();
			return;
		}
		EnterFieldAction action = new EnterFieldAction(army, posRecord.getMapId(), posRecord.getMapTempId(), posRecord.getPos());
		army.enqueue(action);

	}

}
