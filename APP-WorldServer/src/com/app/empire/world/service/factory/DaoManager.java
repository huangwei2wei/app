package com.app.empire.world.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.dao.mongo.impl.HeroSkillDao;

@Service
public class DaoManager {
	private static DaoManager daoManager;
	@Autowired
	private HeroSkillDao heroSkillDao;

	public static DaoManager getManager() {
		return daoManager;
	}

	public void setDaoManager(DaoManager daoManager) {
		DaoManager.daoManager = daoManager;
	}

	public HeroSkillDao getHeroSkillDao() {
		return heroSkillDao;
	}

}
