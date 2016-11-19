package com.app.empire.scene.service.campaign.action;

import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.state.CampaignState;
import com.chuangyou.xianni.constant.CampaignConstant.CampaignType;
import com.chuangyou.xianni.exec.Action;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.world.ArmyProxy;

public class CampaignLeaveAction extends Action {
	ArmyProxy	army;
	Campaign	campaign;
	boolean		unline;

	public CampaignLeaveAction(Campaign campaign, ArmyProxy army) {
		super(campaign);
		this.army = army;
		this.campaign = campaign;
	}

	public CampaignLeaveAction(Campaign campaign, ArmyProxy army, boolean unLine) {
		super(campaign);
		this.army = army;
		this.campaign = campaign;
		this.unline = unLine;
	}

	@Override
	public void execute() {
		if (this.unline == false) {
			PBMessage quit = new PBMessage(Protocol.C_QUIT_CAMPAIGN);
			army.sendPbMessage(quit);
		}
		campaign.removeArmy(army);

		campaign.setExpiredTime(System.currentTimeMillis() + 30 * 60 * 1000);
		/** 组队副本，当所有人离开时，销毁副本 */
		if (campaign.getTemp().getType() == CampaignType.TEAM && campaign.isEmpty() && campaign.getState().getCode() != CampaignState.STOP) {
			campaign.over();
		} else {
			campaign.sendCampaignInfo(army);
		}
		army.getPlayer().removeCampaignBuffer();
	}

}
