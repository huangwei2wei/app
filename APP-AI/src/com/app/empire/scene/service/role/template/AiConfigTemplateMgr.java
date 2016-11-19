package com.app.empire.scene.service.role.template;

import java.util.HashMap;
import java.util.Map;

import com.chuangyou.xianni.entity.spawn.AiConfig;
import com.chuangyou.xianni.sql.dao.DBManager;

public class AiConfigTemplateMgr {
	public static Map<Integer, AiConfig> aiConfigTemps = new HashMap<Integer, AiConfig>();

	public static boolean init() {
		return reloadAiConfigTemp();
	}

	public static boolean reloadAiConfigTemp() {
		aiConfigTemps = DBManager.getAiConfigDao().getAll();
		if (aiConfigTemps == null)
			return false;
		return true;
	}

	public static AiConfig get(int id) {
		return aiConfigTemps.get(id);
	}
}
