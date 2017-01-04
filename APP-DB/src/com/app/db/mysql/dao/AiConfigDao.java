package com.app.db.mysql.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.app.db.mysql.base.dao.impl.UniversalDaoHibernate;
import com.app.db.mysql.entity.AiConfig;

/**
 * The DAO class for the BaseLanguage entity.
 */
@Repository
public class AiConfigDao extends UniversalDaoHibernate {

	public Map<Integer, AiConfig> loadAiConfig() {
		Map<Integer, AiConfig> aiConfigTemps = new HashMap<Integer, AiConfig>();
		List<AiConfig> rsl = getAll(AiConfig.class);
		for (AiConfig aiConfig : rsl) {
			aiConfigTemps.put(aiConfig.getId(), aiConfig);
		}
		return aiConfigTemps;
	}

}