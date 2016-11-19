package com.app.empire.scene.service.battle.mgr;

import java.util.concurrent.atomic.AtomicLong;

public class ObjectIdBuilder {
	private static AtomicLong BUFFER_ID = new AtomicLong(1);

	public static long getBufferId() {
		synchronized (BUFFER_ID) {
			return BUFFER_ID.getAndIncrement();
		}
	}
}
