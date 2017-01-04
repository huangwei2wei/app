package com.app.empire.scene.util.engine;

import java.util.concurrent.ScheduledThreadPoolExecutor;
/**
 * 
 * @author doter
 * 
 */
public class Engine {
	public static int ExecutorThreadPoolSize = 10;
	private static ScheduledThreadPoolExecutor executor;// 延迟执行的线程池
	private static HandlerThreadPool threadPool;// 及时执行的线程池

	public static ScheduledThreadPoolExecutor getScheduledExecutor() {
		if (Engine.executor == null) {
			Engine.executor = new ScheduledThreadPoolExecutor(Engine.ExecutorThreadPoolSize, new NamedThreadFactory("Scheduled"));
		}
		return Engine.executor;
	}

	public static HandlerThreadPool getTimelyExecutor() {
		if (Engine.threadPool == null) {
			Engine.threadPool = new HandlerThreadPool(30, 300, 500);
		}
		return threadPool;
	}

}
