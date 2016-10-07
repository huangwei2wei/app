package com.app.empire.scene.service.campaign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CampaignMgr {
	// 副本地图
	public static Map<Integer, Campaign> campagins = new ConcurrentHashMap<>();

	public static boolean init() {
		return false;
	}

	// 添加副本
	public static void add(Campaign campaign) {
		campagins.put(campaign.getIndexId(), campaign);
	}

	public static Campaign getCampagin(int id) {
		return campagins.get(id);
	}

	public static Campaign remove(int id) {
		synchronized (campagins) {
			return campagins.remove(id);
		}

	}

	public static void clearInvalid() {
		List<Campaign> cattrr = new ArrayList<>();
		synchronized (campagins) {
			cattrr.addAll(campagins.values());
		}
		for (Campaign c : cattrr) {
			if (c.isExpried()) {
				c.over();
			}
			if (c.isClear()) {
				c.clearCampaignData();
			}
		}
	}
}
