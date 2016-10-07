package com.app.empire.scene.service.campaign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chuangyou.xianni.entity.campaign.CampaignTemplateInfo;
import com.chuangyou.xianni.sql.dao.DBManager;

public class CampaignTempMgr {
	public static Map<Integer, CampaignTemplateInfo> campaignTemps = new HashMap<>();

	public static boolean init() {
		reload();
		return true;
	}

	public static boolean reload() {
		List<CampaignTemplateInfo> campaignTemplateInfos = DBManager.getCampaignTemplateInfoDao().getAll();
		if (campaignTemplateInfos == null || campaignTemplateInfos.size() == 0) {
			return false;
		}
		for (CampaignTemplateInfo info : campaignTemplateInfos) {
			campaignTemps.put(info.getTemplateId(), info);
		}
		return true;
	}

	public static CampaignTemplateInfo get(int tempId) {
		return campaignTemps.get(tempId);
	}

}
