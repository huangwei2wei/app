package com.app.empire.scene.util;

public class LockData {
	private boolean status = false;

	public LockData() {
	}

	public synchronized boolean beginLock() {
		if (status) {
			return false;
		}
		status = true;
		return status;
	}

	public synchronized boolean commitLock() {
		status = false;
		return status;
	}

}
