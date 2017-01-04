package com.app.empire.scene.service.campaign;

import net.sf.json.util.JSONUtils;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.CampaignInfo;
import com.app.empire.scene.constant.CampaignConstant.CampaignType;
import com.app.empire.scene.service.world.ArmyProxy;

public class CampaignFactory {
	protected static Logger log = Logger.getLogger(CampaignFactory.class);

	/**
	 * 创建副本
	 */
	public static Campaign createCampaign(CampaignInfo tempInfo, ArmyProxy creater, int taskId) {
		switch (tempInfo.getType()) {
		case CampaignType.SINGLE:
			return new SingleCampaign(tempInfo, creater, taskId);
			// case CampaignType.TEAM:
			// return new TeamCampaign(tempInfo, creater, taskId);
//		case CampaignType.AVATAR:
//			return new AvatarCampaign(tempInfo, creater);
//		case CampaignType.BEAD:
//			return new InverseBeadCampaign(tempInfo, creater, taskId);
		case CampaignType.THE_PLANE:
			return new PlaneCampaign(tempInfo, creater, taskId);
//		case CampaignType.NPC_CAM_ACTIVITY:
//			return new NpcCamActivityCampaign(tempInfo, creater, taskId);
		case CampaignType.PLOT:
			return new Campaign(tempInfo, creater, taskId);
		default:
			break;
		}
		log.error("the campaignType not exists ," + JSONUtils.valueToString(tempInfo));
		return null;
	}
}
