package com.app.db.mysql.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.app.db.mysql.base.dao.impl.UniversalDaoHibernate;
import com.app.db.mysql.entity.CampaignInfo;

/**
 * The DAO class for the BaseLanguage entity.
 */
@Repository
public class CampaignInfoDao extends UniversalDaoHibernate {

	public Map<Integer, CampaignInfo> loadCampaignInfo() {
		Map<Integer, CampaignInfo> campaignTemps = new HashMap<>();
		List<CampaignInfo> rsl = getAll(CampaignInfo.class);
		for (CampaignInfo campaignInfo : rsl) {
			campaignTemps.put(campaignInfo.getTemplateId(), campaignInfo);
		}
		return campaignTemps;
	}

}