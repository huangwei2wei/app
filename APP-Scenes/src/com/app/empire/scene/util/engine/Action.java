package com.app.empire.scene.util.engine;

import org.apache.log4j.Logger;

/**
 * 立即执行，不是线程安全的
 * 
 * @author doter
 * 
 */
public abstract class Action implements Runnable {
	protected Logger log = Logger.getLogger(Action.class);

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try {
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

	public void start() {
		Engine.getTimelyExecutor().execute(this);
	}

	public abstract void execute();
}
