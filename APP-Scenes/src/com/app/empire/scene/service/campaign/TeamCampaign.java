//package com.app.empire.scene.service.campaign;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.aspectj.bridge.MessageUtil;
//
//import com.app.empire.protocol.Protocol;
//import com.app.empire.scene.constant.CampaignConstant.CampaignStatu;
//import com.app.empire.scene.entity.CampaignInfo;
//import com.app.empire.scene.service.campaign.state.CampaignState;
//import com.app.empire.scene.service.world.ArmyProxy;
//import com.app.empire.scene.service.world.WorldMgr;
//
//public class TeamCampaign extends Campaign {
//
//	public TeamCampaign(CampaignInfo tempInfo, ArmyProxy creater, int taskId) {
//		super(tempInfo, creater, taskId);
//	}
//
//	public boolean agreedToEnter(ArmyProxy army) {
//		if (!super.agreedToEnter(army)) {
//			return false;
//		}
//
//		if (army.getPlayerId() == creater) {
//			return true;
//		}
//		Team team = TeamMgr.getTeam(creater);
//		if (team == null) {
//			return false;
//		}
//		return team.inTeam(army.getPlayerId());
//	}
//
//	/**
//	 * 副本结束
//	 */
//	public void over() {
//		super.over();
//		// state = new StopState(this);
//		//
//		// CampaignStatuMsg.Builder cstatu = CampaignStatuMsg.newBuilder();
//		// cstatu.setCampaignId(getIndexId());
//		// cstatu.setStatu(0);// 退出
//		// PBMessage statuMsg =
//		// MessageUtil.buildMessage(Protocol.C_CAMPAIGN_STATU, cstatu);
//		//
//		// PassFbInnerMsg.Builder passFbMsg = PassFbInnerMsg.newBuilder();
//		// passFbMsg.setCampaignId(campaignId);
//		// passFbMsg.setTeamId(teamId);
//		// for (ArmyProxy army : armys.values()) {
//		// sendCampaignInfo(army);
//		// onPlayerLeave(army);
//		// // 通知center服务器,玩家副本销毁了
//		// army.sendPbMessage(statuMsg);
//		// passFbMsg.addPlayers(army.getPlayerId());
//		// }
//		//
//		// PBMessage passFbpkg =
//		// MessageUtil.buildMessage(Protocol.C_REQ_PASS_FB, passFbMsg);
//		// GatewayLinkedSet.send2Server(passFbpkg);
//		// setExpiredTime(System.currentTimeMillis() + 5 * 60 * 1000);
//
//		// Team team = TeamMgr.getTeam(creater);
//		// if (team != null) {
//		// List<Long> members = team.getMembers();
//		// for (long playerId : members) {
//		// ArmyProxy army = WorldMgr.getArmy(playerId);
//		// if (army != null) {
//		// sendCampaignInfo(army);
//		// }
//		// }
//		// }
//	}
//
//	public void onKick(ArmyProxy army) {
//
//		/** 组队副本，当所有人离开时，销毁副本 */
//		if (isEmpty() && getState().getCode() != CampaignState.STOP) {
//			over();
//		} else {
//			CampaignStatuMsg.Builder cstatu = CampaignStatuMsg.newBuilder();
//			cstatu.setIndexId(getIndexId());
//			cstatu.setTempId(campaignId);
//			cstatu.setStatu(CampaignStatu.NOTITY2C_OUT);// 退出
//			PBMessage statuMsg = MessageUtil.buildMessage(Protocol.C_CAMPAIGN_STATU, cstatu);
//			army.sendPbMessage(statuMsg);
//
//			PBMessage quit = new PBMessage(Protocol.C_QUIT_CAMPAIGN);
//			army.sendPbMessage(quit);
//			removeArmy(army);
//
//			CampaignInfoMsg.Builder infoMsg = CampaignInfoMsg.newBuilder();
//			infoMsg.setId(id);
//			infoMsg.setCount(armys.size());
//			infoMsg.setCreaterId(creater);
//			infoMsg.setState(CampaignState.STOP);
//			infoMsg.setTempId(campaignId);
//			PBMessage message = MessageUtil.buildMessage(Protocol.U_CAMPAIGN_INFO, infoMsg);
//			army.sendPbMessage(message);
//		}
//	}
//
//	public void setTeamId(int teamId) {
//		this.teamId = teamId;
//	}
//
//	public List<ArmyProxy> getAllArmys() {
//		Team team = TeamMgr.getTeam(creater);
//		if (team != null) {
//			List<ArmyProxy> all = new ArrayList<>();
//			List<Long> members = team.getMembers();
//			for (long playerId : members) {
//				ArmyProxy army = WorldMgr.getArmy(playerId);
//				if (army != null) {
//					all.add(army);
//				}
//			}
//			return all;
//		} else {
//			return super.getAllArmys();
//		}
//	}
//
// }
