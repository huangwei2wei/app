package com.app.empire.scene.service.campaign;

import com.chuangyou.common.util.JSONUtil;
import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.constant.CampaignConstant.CampaignType;
import com.chuangyou.xianni.entity.campaign.CampaignTemplateInfo;
import com.chuangyou.xianni.inverseBead.InverseBeadCampaign;
import com.chuangyou.xianni.world.ArmyProxy;

public class CampaignFactory {

	/**
	 * 创建副本
	 */
	public static Campaign createCampaign(CampaignTemplateInfo tempInfo, ArmyProxy creater, int taskId) {
		switch (tempInfo.getType()) {
			case CampaignType.SINGLE:
				return new SingleCampaign(tempInfo, creater, taskId);
			case CampaignType.TEAM:
				return new TeamCampaign(tempInfo, creater, taskId);
			case CampaignType.CHALLENG:
				return new ChallengeCampaign(tempInfo, creater, taskId);
			case CampaignType.BEAD:
				return new InverseBeadCampaign(tempInfo, creater, taskId);
			default:
				break;
		}
		Log.error("the campaignType not exists ," + JSONUtil.getJSONString(tempInfo));
		return null;
	}
}
