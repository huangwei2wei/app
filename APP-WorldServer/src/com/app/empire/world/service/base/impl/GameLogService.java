package com.app.empire.world.service.base.impl;

import java.io.Serializable;
import java.util.concurrent.ThreadPoolExecutor;

import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.db.mysql.dao.impl.BaseDaoSupport;
import com.app.empire.world.dao.mysql.gameLog.impl.LogAccountLoginDao;
import com.app.empire.world.entity.mysql.gameLog.LogAccountLogin;
import com.app.thread.ThreadPool;

/**
 * 游戏日志记录(异步)
 * 
 * @author doter
 */
@Service
public class GameLogService {
	private HandlerThreadPool threadPool = new HandlerThreadPool(2, 4, 100);
	@Autowired
	private LogAccountLoginDao logAccountLoginDao;
	//记录账号登录日志
	public void saveLogAccountLogin(LogAccountLogin entity){
		saveLog(logAccountLoginDao, entity);
	}
	
	private void saveLog(BaseDaoSupport dao, Serializable entity) {
		try {
			threadPool.execute(new LogThreadTask(dao, entity));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class HandlerThreadPool extends ThreadPool {
		/**
		 * 构造函数，初始化线程池及队列
		 * 
		 * @param minPoolSize
		 *            最小线程数
		 * @param maxPoolSize
		 *            最大线程数
		 * @param queurSize
		 *            等待队列大小
		 */
		public HandlerThreadPool(int minPoolSize, int maxPoolSize, int queurSize) {
			super(minPoolSize, maxPoolSize, queurSize);
		}

		/**
		 * 线程池已满,拒绝线程
		 * 
		 * @param runnable
		 *            任务信息
		 * @param threadPoolExecutor
		 *            异常信息
		 */
		@Override
		public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
			System.err.println("LogThreadPool Is Full...");
		}
	}
	
	static class LogThreadTask implements Runnable {
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
				Log.info(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
