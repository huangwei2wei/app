package com.app.empire.world.logs;

import java.io.Serializable;

import org.springframework.transaction.annotation.Transactional;

import com.app.db.mysql.dao.impl.BaseDaoSupport;

public class LogThreadTask implements Runnable {
	private BaseDaoSupport dao;
	private Serializable entity;

	// 构造函数
	public LogThreadTask(BaseDaoSupport dao, Serializable entity) {
		this.dao = dao;
		this.entity = entity;
	}

	@Override
	public void run() {
		try {
			dao.save(entity);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}