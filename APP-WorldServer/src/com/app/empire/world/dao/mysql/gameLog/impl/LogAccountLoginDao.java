package com.app.empire.world.dao.mysql.gameLog.impl;

import org.springframework.stereotype.Repository;

import com.app.db.mysql.dao.impl.GenericDaoHibernate;
import com.app.empire.world.entity.mysql.gameLog.LogAccountLogin;

/**
 * The DAO class for the LogAccountLogin entity.
 */
@Repository
public class LogAccountLoginDao extends GenericDaoHibernate<LogAccountLogin, Integer> {
	public LogAccountLoginDao() {
		super(LogAccountLogin.class);
	}

	// @Transactional(readOnly=false)
	public void save(LogAccountLogin entity) {
		super.save(entity);
	}
	// public List<LogAccountLogin> getAll() {
	// return super.getAll();
	// }

}