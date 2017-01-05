package com.app.thread.engine;

import java.util.concurrent.ThreadPoolExecutor;

import com.app.thread.ThreadPool;

class HandlerThreadPool extends ThreadPool {
	/**
	 * 构造函数，初始化线程池及队列
	 * 
	 * @param minPoolSize 最小线程数
	 * @param maxPoolSize 最大线程数
	 * @param queurSize 等待队列大小
	 */
	public HandlerThreadPool(int minPoolSize, int maxPoolSize, int queurSize) {
		super(minPoolSize, maxPoolSize, queurSize);
	}

	/**
	 * 线程池已满,拒绝线程
	 * 
	 * @param runnable 任务信息
	 * @param threadPoolExecutor 异常信息
	 */
	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
		System.err.println("ThreadPool Is Full...");
	}
}