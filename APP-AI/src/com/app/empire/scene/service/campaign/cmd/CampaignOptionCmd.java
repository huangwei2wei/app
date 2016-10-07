package com.app.empire.scene.service.campaign.cmd;

import com.app.empire.scene.service.team.Team;
import com.app.empire.scene.service.team.TeamMgr;
import com.chuangyou.common.protobuf.pb.campaign.CampaignOptionMsgProto.CampaignOptionMsg;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.common.ErrorCode;
import com.chuangyou.xianni.common.error.ErrorMsgUtil;
import com.chuangyou.xianni.constant.CampaignConstant;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_CAMPAIGN_OPTION, desc = "副本操作")
public class CampaignOptionCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		CampaignOptionMsg reqMsg = CampaignOptionMsg.parseFrom(packet.getBytes());
		int op = reqMsg.getOptionType();
		int parm1 = reqMsg.getParam1();

		// 请求副本信息
		if (op == CampaignConstant.GET_INFO) {
			Campaign campaign = CampaignMgr.getCampagin(parm1);
			if (campaign != null) {
				campaign.sendCampaignInfo(army);
			} else {
				ErrorMsgUtil.sendErrorMsg(army, ErrorCode.CAMPAIGN_NOT_EXIST, Protocol.S_CAMPAIGN_OPTION, "");
			}
		}
		// 返回副本
		if (op == CampaignConstant.JOIN) {
			Campaign campaign = CampaignMgr.getCampagin(parm1);
			if (campaign != null) {
				campaign.onPlayerEnter(army);
			}
		}

		// 离开副本
		if (op == CampaignConstant.LEAVE) {
			Campaign campaign = CampaignMgr.getCampagin(parm1);
			if (campaign != null) {
				campaign.onPlayerLeave(army);
			}
		}
		/* 进入队伍所在副本 */
		if (op == CampaignConstant.JOIN_TEAM) {
			Team tem = TeamMgr.getTeam(army.getPlayerId());
			if (tem != null && tem.getCampaignId() != 0) {
				Campaign campaign = CampaignMgr.getCampagin(tem.getCampaignId());
				if (campaign != null) {
					campaign.onPlayerEnter(army);
				}
			}
		}
	}

}
