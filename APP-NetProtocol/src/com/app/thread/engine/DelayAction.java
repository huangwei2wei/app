package com.app.thread.engine;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * 周期性执行任务，不是线程安全的
 * 
 * @author doter
 * 
 */
public abstract class DelayAction implements Runnable {
	protected Logger log = Logger.getLogger(DelayAction.class);
	private long initialDelay;// 延迟
	private long period;// 周期，0非重复任务。
	private ScheduledFuture<?> t;

	public DelayAction(long initialDelay, long period) {
		this.initialDelay = initialDelay;
		this.period = period;
	}

	@Override
	public void run() {
		// System.out.println("-------------run-------------" + this.toString());
		try {
			long start = System.currentTimeMillis();
			execute();
			long end = System.currentTimeMillis();
			long interval = end - start;
			if (interval >= 1000) {
				log.warn("execute action : " + this.toString() + ", interval : " + interval);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("run action execute exception. action : " + this.toString(), e);
		}
	}

	/**
	 * 固定间隔时间周期性执行任务
	 */
	public void startWithFixedDelay() {
		if (t == null) {
			t = Engine.getScheduledExecutor().scheduleWithFixedDelay(this, this.initialDelay, this.period, TimeUnit.MILLISECONDS);// 定时触发
		} else {
			log.error("任务已经开始。");
		}
	}

	/**
	 * 固定时间周期性执行任务
	 */
	public void startWithFixedRate() {
		if (t == null) {
			t = Engine.getScheduledExecutor().scheduleAtFixedRate(this, this.initialDelay, this.period, TimeUnit.MILLISECONDS);// 定时触发
		} else {
			log.error("任务已经开始。");
		}
	}

	/**
	 * 延期性执行，只执行一次
	 */
	public void startOneTime() {
		if (t == null) {
			t = Engine.getScheduledExecutor().schedule(this, this.initialDelay, TimeUnit.MILLISECONDS);
		} else {
			log.error("任务已经开始。");
		}
	}

	/**
	 * 对象销毁时需要调用，否则内存泄漏
	 */
	public boolean stop() {
		if (t == null) {
			log.error("任务未开始!");
			return false;
		}
		boolean res = t.cancel(true);
		t = null;
		// System.out.println("停止---" + this.toString() + "  res:" + res);
		return res;
	}

	public abstract void execute();
}
