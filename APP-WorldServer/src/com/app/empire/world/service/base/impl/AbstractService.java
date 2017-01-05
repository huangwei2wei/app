//package com.app.empire.world.service.base.impl;
//
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//
//import org.apache.log4j.Logger;
//import org.springframework.stereotype.Service;
//
//import com.app.empire.world.exception.ErrorMessages;
//import com.app.empire.world.session.AbstractInfo;
//import com.app.empire.world.session.HandlerUtil;
//import com.app.protocol.data.AbstractData;
//import com.app.protocol.exception.ProtocolException;
//import com.app.session.Session;
//import com.app.thread.ThreadPool;
//
///**
// * 消息处理类
// * 
// * @author doter
// */
//@Service
//public class AbstractService implements Runnable {
//	private Logger log = Logger.getLogger(AbstractService.class);
//	private HandlerThreadPool threadPool = new HandlerThreadPool(30, 300, 500);
//	private LinkedBlockingQueue<AbstractInfo> abstractList = new LinkedBlockingQueue<AbstractInfo>(1024);
//
//	public AbstractService() {
//		start();
//	}
//
//	public void start() {
//		Thread t0 = new Thread(this);
//		t0.setName("AbstractService-Thread0");
//		t0.start();
//	}
//
//	public void run() {
//		while (true) {
//			try {
//				AbstractInfo abstractInfo = abstractList.take();
//				// System.out.println("runing sessionKey:"+abstractInfo.getSessionKey()+"------- type:"+abstractInfo.getDataobj().getType()+"----------subtype:"+abstractInfo.getDataobj().getSubType());
//				threadPool.execute(new ThreadTask(abstractInfo));
//			} catch (Throwable e) {
//				log.error(e, e);
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public void addAbstractInfo(AbstractData dataobj, Session session) {
//		try {
//			abstractList.put(new AbstractInfo(dataobj, session));
//		} catch (InterruptedException e) {
//			log.error(e.toString());
//		}
//	}
//
//	class HandlerThreadPool extends ThreadPool {
//		/**
//		 * 构造函数，初始化线程池及队列
//		 * 
//		 * @param minPoolSize
//		 *            最小线程数
//		 * @param maxPoolSize
//		 *            最大线程数
//		 * @param queurSize
//		 *            等待队列大小
//		 */
//		public HandlerThreadPool(int minPoolSize, int maxPoolSize, int queurSize) {
//			super(minPoolSize, maxPoolSize, queurSize);
//		}
//
//		/**
//		 * 线程池已满,拒绝线程
//		 * 
//		 * @param runnable
//		 *            任务信息
//		 * @param threadPoolExecutor
//		 *            异常信息
//		 */
//		@Override
//		public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
//			System.err.println("ThreadPool Is Full...");
//			ThreadTask threadTask = (ThreadTask) runnable;
//			AbstractData abstractData = threadTask.getAbstractData();
//			Session session = threadTask.getSession();
//			if (abstractData != null && session != null) {
//				session.sendError(new ProtocolException(ErrorMessages.LOGIN_ERROR_MESSAGE, abstractData.getSerial(), abstractData
//						.getSessionId(), abstractData.getType(), abstractData.getSubType()));
//			}
//		}
//	}
//
//	class ThreadTask implements Runnable {
//		// 协议信息
//		private AbstractInfo abstractInfo = null;
//
//		// 构造函数，　
//		public ThreadTask(AbstractInfo abstractInfo) {
//			this.abstractInfo = abstractInfo;
//		}
//
//		@Override
//		public void run() {
//			try {
//				HandlerUtil.doHandler(this.abstractInfo.getSession(), this.abstractInfo.getDataobj());
//			} catch (Throwable e) {
//				log.error(e, e);
//				e.printStackTrace();
//			} finally {
//				// System.out.println("removeSessionIdSet getSessionKey:"+this.abstractInfo.getSessionKey()+"--------- type:"+this.abstractInfo.getDataobj().getType()+"----------subtype:"+this.abstractInfo.getDataobj().getSubType());
//			}
//		}
//
//		/**
//		 * 获取协议信息
//		 * 
//		 * @return 协议信息
//		 */
//		public AbstractData getAbstractData() {
//			return this.abstractInfo.getDataobj();
//		}
//
//		/**
//		 * 获取会话信息
//		 * 
//		 * @return 返回会放信息
//		 */
//		public Session getSession() {
//			return this.abstractInfo.getSession();
//		}
//	}
//
// }
