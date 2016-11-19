package com.app.empire.scene.service.role.helper;

import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.pool.ObjectPool;

public class HatredManager {
	
	public ObjectPool<Hatred> pool = new ObjectPool<Hatred>(10000);
	
	private static Object locker = new Object();
	private static HatredManager manager;
	
	private HatredManager() {}
	
	public static HatredManager getInstance()
	{
		synchronized (locker) {
			if(manager == null)
				manager = new HatredManager();
		}
		return manager;
	}
	
	public Hatred getHatred()
	{
		try {
			return pool.get(Hatred.class);
		} catch (Exception e) {
			// TODO: handle exception
			Log.error(e);
		}
		return null;
	}

	public void removeHatred(Hatred hatred)
	{
		pool.put(hatred);
	}
}
