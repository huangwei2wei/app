package com.app.empire.scene.service.campaign.action;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.campaign.CampaignInfoMsgProto.CampaignInfoMsg;
import com.app.empire.protocol.pb.campaign.CampaignStatuMsgProto.CampaignStatuMsg;
import com.app.empire.scene.constant.CampaignConstant.CampaignStatu;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.state.CampaignState;
import com.app.empire.scene.service.warField.action.EnterFieldAction;
import com.app.empire.scene.service.world.ArmyPositionRecord;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.thread.exec.Action;

/**
 * 玩家彻底退出副本，被踢出副本
 * 
 */
public class CampaignExitAction extends Action {

	ArmyProxy army;
	Campaign campaign;

	public CampaignExitAction(Campaign campaign, ArmyProxy army) {
		super(campaign);
		this.army = army;
		this.campaign = campaign;
	}

	@Override
	public void execute() {
		CampaignStatuMsg.Builder cstatu = CampaignStatuMsg.newBuilder();
		cstatu.setIndexId(campaign.getIndexId());
		cstatu.setTempId(campaign.getTemp().getTemplateId());
		cstatu.setStatu(CampaignStatu.NOTITY2C_OUT);// 退出
		army.sendPbMessage(Protocol.MAIN_CAMPAIGN, Protocol.CAMPAIGN_Statu, cstatu.build());

		campaign.removeArmy(army, true);
		army.getPlayer().removeCampaignBuffer();

		CampaignInfoMsg.Builder infoMsg = CampaignInfoMsg.newBuilder();
		infoMsg.setId(campaign.getIndexId());
		infoMsg.setCount(campaign.getAllArmys().size());
		infoMsg.setCreaterId(campaign.getCreaterId());
		infoMsg.setState(CampaignState.STOP);
		infoMsg.setTempId(campaign.getTemp().getTemplateId());
		// PBMessage message = MessageUtil.buildMessage(Protocol.U_CAMPAIGN_INFO, infoMsg);
		army.sendPbMessage(Protocol.MAIN_CAMPAIGN, Protocol.CAMPAIGN_Info, infoMsg.build());

		// if (campaign instanceof PlaneCampaign) {
		// int outFiledId = campaign.getTemp().getTemplateId() % (Field.MAX_ID);
		// EnterFieldAction action = new EnterFieldAction(army, outFiledId, outFiledId, army.getPlayer().getPostion());
		// army.enqueue(action);
		// } else {
		// 退出至上一个节点
		ArmyPositionRecord posRecord = army.getPosRecord();
		if (posRecord == null) {
			army.returnBornMap();
			return;
		}
		EnterFieldAction action = new EnterFieldAction(army, posRecord.getMapId(), posRecord.getMapTempId(), posRecord.getPos());
		army.enqueue(action);
		// }
	}

}
