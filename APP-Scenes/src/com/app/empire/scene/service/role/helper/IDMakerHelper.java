package com.app.empire.scene.service.role.helper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IDMakerHelper {
	private static AtomicInteger current = new AtomicInteger(100000);
	private static AtomicInteger NPC_ID = new AtomicInteger(1000000000);
	private static AtomicLong BUFFER_ID = new AtomicLong(1);
	private static AtomicInteger CAMPAIGN_ID = new AtomicInteger(1);
	private static AtomicInteger DROP_ID = new AtomicInteger(1);

	private static AtomicLong ATTACK_ID = new AtomicLong(1);

	/**
	 * 生成id
	 * 
	 * @return
	 */
	public static int nextFieldId() {
		synchronized (current) {
			return current.incrementAndGet();
		}
	}

	public static int nextID() {
		synchronized (NPC_ID) {
			return NPC_ID.incrementAndGet();
		}
	}

	public static long bufferId() {
		synchronized (BUFFER_ID) {
			return BUFFER_ID.incrementAndGet();
		}
	}

	public static int dropId() {
		synchronized (DROP_ID) {
			return DROP_ID.incrementAndGet();
		}
	}

	public static int campaignId() {
		synchronized (CAMPAIGN_ID) {
			return CAMPAIGN_ID.incrementAndGet();
		}
	}

	public static long attackId() {
		synchronized (ATTACK_ID) {
			return ATTACK_ID.incrementAndGet();
		}
	}
}
