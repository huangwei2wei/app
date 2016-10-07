package com.app.empire.scene.service.warfield.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chuangyou.xianni.entity.field.FieldInfo;
import com.chuangyou.xianni.sql.dao.DBManager;
import com.chuangyou.xianni.sql.dao.FieldInfoDao;

public class FieldTemplateMgr {
	public static Map<Integer, FieldInfo>					fieldTemps				= new HashMap<Integer, FieldInfo>();

	// 副本地图信息
	private static Map<Integer, Map<Integer, FieldInfo>>	campaignFieldInfoMaps	= new HashMap<Integer, Map<Integer, FieldInfo>>();

	public static boolean init() {
		return reloadFieldInfoTemp();
	}

	public static boolean reloadFieldInfoTemp() {
		FieldInfoDao dao = DBManager.getFieldInfoDao();
		List<FieldInfo> info = dao.getAll();
		for (FieldInfo f : info) {
			fieldTemps.put(f.getMapKey(), f);
			if (f.getType() == 2) {
				if (!campaignFieldInfoMaps.containsKey(f.getCampaignId())) {
					campaignFieldInfoMaps.put(f.getCampaignId(), new HashMap<>());
				}
				Map<Integer, FieldInfo> container = campaignFieldInfoMaps.get(f.getCampaignId());
				container.put(f.getCampaignIndex(), f);
			}
		}
		return true;
	}

	public static FieldInfo getFieldTemp(int mapKey) {
		return fieldTemps.get(mapKey);
	}

	/** 获取副本地图数据 */
	public static Map<Integer, FieldInfo> getCFieldInfos(int campaignId) {
		return campaignFieldInfoMaps.get(campaignId);
	}

}
