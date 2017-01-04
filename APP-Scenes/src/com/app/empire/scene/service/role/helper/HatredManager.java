package com.app.empire.scene.service.role.helper;

import org.apache.log4j.Logger;

import com.app.empire.scene.util.pool.ObjectPool;

public class HatredManager {
	protected Logger log = Logger.getLogger(HatredManager.class);
	public ObjectPool<Hatred> pool = new ObjectPool<Hatred>(10000);

	private static HatredManager manager = new HatredManager();

	private HatredManager() {
	}

	public static HatredManager getInstance() {
		return manager;
	}

	public Hatred getHatred() {
		try {
			return pool.get(Hatred.class);
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public void removeHatred(Hatred hatred) {
		pool.put(hatred);
	}
}
