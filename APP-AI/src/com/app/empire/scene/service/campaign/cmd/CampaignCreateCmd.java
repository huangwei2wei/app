package com.app.empire.scene.service.campaign.cmd;

import com.chuangyou.common.protobuf.pb.campaign.CreateCampaignMsgProto.CreateCampaignMsg;
import com.chuangyou.xianni.campaign.action.CampaignCreateAction;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_CREATE_CAMPAIGN, desc = "创建副本")
public class CampaignCreateCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		CreateCampaignMsg msg = CreateCampaignMsg.parseFrom(packet.getBytes());
		//CampaignCreateAction createAction = new CampaignCreateAction(army, msg.getCampaign(), 2);
		CampaignCreateAction createAction = new CampaignCreateAction(army, msg.getCampaign(), msg.getTaskId());
		army.enqueue(createAction);
	}

}
