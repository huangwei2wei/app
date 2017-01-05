package com.app.thread.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

/**
 * 执行action队列的线程池<br>
 * 延迟执行的action，先放置到delay action队列中，延迟时间后再加入执行队列
 */
public class ActionExecutor {
	private Logger log = Logger.getLogger(Action.class);
	private AbstractActionQueue defaultQueue;
	private ThreadPoolExecutor pool;
	private DelayCheckThread delayCheckThread;

	/**
	 * 执行action队列的线程池
	 * 
	 * @param corePoolSize 最小线程数，包括空闲线程
	 * @param maxPoolSize 最大线程数
	 * @param keepAliveTime 当线程数大于核心时，终止多余的空闲线程等待新任务的最长时间
	 * @param cacheSize 执行队列大小
	 * @param prefix 线程池前缀名称
	 */
	public ActionExecutor(int corePoolSize, int maxPoolSize, int keepAliveTime, int cacheSize, String prefix) {
		TimeUnit unit = TimeUnit.MINUTES;
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
		if (prefix == null) {
			prefix = "";
		}
		ThreadFactory threadFactory = new Threads(prefix);
		pool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		defaultQueue = new AbstractActionQueue(this);

		delayCheckThread = new DelayCheckThread(prefix);
		delayCheckThread.start();
	}

	public AbstractActionQueue getDefaultQueue() {
		return defaultQueue;
	}

	public void enDefaultQueue(Action action) {
		defaultQueue.enqueue(action);
	}

	public void execute(Action action) {
		pool.execute(action);
	}

	public void enDelayQueue(DelayAction delayAction) {
		delayCheckThread.addAction(delayAction);
	}

	public void stop() {
		delayCheckThread.stopping();
		if (!pool.isShutdown()) {
			pool.shutdown();
		}

	}

	static class Threads implements ThreadFactory {

		static final AtomicInteger poolNumber = new AtomicInteger(1);
		final ThreadGroup group;
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;

		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(group, runnable, (new StringBuilder()).append(namePrefix).append(threadNumber.getAndIncrement()).toString(), 0L);
			if (thread.isDaemon())
				thread.setDaemon(false);
			if (thread.getPriority() != 5)
				thread.setPriority(5);
			return thread;
		}

		Threads(String prefix) {
			SecurityManager securitymanager = System.getSecurityManager();
			group = securitymanager == null ? Thread.currentThread().getThreadGroup() : securitymanager.getThreadGroup();
			namePrefix = (new StringBuilder()).append("pool-").append(poolNumber.getAndIncrement()).append("-").append(prefix).append("-thread-").toString();
		}
	}

	class DelayCheckThread extends Thread {

		private static final int FRAME_PER_SECOND = 120;
		private Object lock = new Object(); // 线程锁
		private List<DelayAction> queue;
		private List<DelayAction> execQueue;
		private boolean isRunning;

		public DelayCheckThread(String prefix) {
			super(prefix + "DelayCheckThread");
			queue = new ArrayList<DelayAction>();
			execQueue = new ArrayList<DelayAction>();
			isRunning = true;
			setPriority(Thread.MAX_PRIORITY); // 给予高优先级
		}

		public boolean isRunning() {
			return isRunning;
		}

		public void stopping() {
			if (isRunning) {
				isRunning = false;
			}
		}

		@Override
		public void run() {
			long balance = 0;
			while (isRunning) {
				try {
					int execute = 0;
					// 读取待执行的队列,如果没有可以执行的动作则等待
					poll();
					if (execQueue.size() == 0) {
						continue;
					}

					long start = System.currentTimeMillis();
					execute = execActions();
					execQueue.clear();
					long end = System.currentTimeMillis();
					long interval = end - start;
					balance += FRAME_PER_SECOND - interval;
					if (interval > FRAME_PER_SECOND) {
						log.warn("DelayCheckThread is spent too much time: " + interval + "ms, execute = " + execute);
					}
					if (balance > 0) {
						Thread.sleep((int) balance);
						balance = 0;
					} else {
						if (balance < -1000) {
							balance += 1000;
						}
					}
				} catch (Exception e) {
					log.error("DelayCheckThread error. ", e);
				}
			}
		}

		/**
		 * 返回执行成功的Action数量
		 **/
		public int execActions() {
			int executeCount = 0;
			for (DelayAction delayAction : execQueue) {
				try {
					long begin = System.currentTimeMillis();
					if (delayAction == null) {
						log.error("error");
					}
					if (!delayAction.canExec(begin)) {
						addAction(delayAction);
					}
					executeCount++;
					long end = System.currentTimeMillis();
					if (end - begin > FRAME_PER_SECOND) {
						log.warn(delayAction.toString() + " spent too much time. time :" + (end - begin));
					}
				} catch (Exception e) {
					log.error("执行action异常" + delayAction.toString(), e);
				}
			}
			return executeCount;
		}

		/**
		 * 添加Action到队列
		 * 
		 * @param delayAction
		 */
		public void addAction(DelayAction delayAction) {
			synchronized (lock) {
				queue.add(delayAction);
				lock.notifyAll();
			}
		}

		/**
		 * 以阻塞的方式读取队列,如果队列为空则阻塞
		 * 
		 * @throws InterruptedException
		 **/
		private void poll() throws InterruptedException {
			synchronized (lock) {
				if (queue.size() == 0) {
					lock.wait();
				} else {
					execQueue.addAll(queue);
					queue.clear();
					lock.notifyAll();
				}
			}
		}
	}
}
