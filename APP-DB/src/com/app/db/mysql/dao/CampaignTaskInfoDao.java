package com.app.db.mysql.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.app.db.mysql.base.dao.impl.UniversalDaoHibernate;
import com.app.db.mysql.entity.CampaignTaskInfo;

@Repository
public class CampaignTaskInfoDao extends UniversalDaoHibernate {
	// private static Map<Integer, CampaignTaskTemplateInfo> taskTemp = new HashMap<>();

	public Map<Integer, CampaignTaskInfo> loadCampaignTaskInfo() {
		Map<Integer, CampaignTaskInfo> taskTemp = new HashMap<>();

		List<CampaignTaskInfo> rsl = getAll(CampaignTaskInfo.class);
		for (CampaignTaskInfo info : rsl) {
			taskTemp.put(info.getId(), info);
		}
		return taskTemp;
	}

}