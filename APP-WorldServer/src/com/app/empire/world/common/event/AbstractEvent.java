package com.app.empire.world.common.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


public abstract class AbstractEvent {
	private Logger											log		= Logger.getLogger(AbstractEvent.class);

	private Hashtable<Integer, Collection<ObjectListener>>	listeners;
	private static boolean									isDebug	= false;

	private Object											lock	= new AbstractEventLock();

	public void addListener(ObjectListener objectListener, int eventType) {
		if (objectListener == null) {
			log.error("-----------eventType-----------------" + eventType);
			return;
		}
		synchronized (lock) {
			if (listeners == null)
				listeners = new Hashtable<Integer, Collection<ObjectListener>>();

			if (eventType == EventNameType.UPDATE_PLAYER_PROPERTY_ALL && listeners.containsKey(EventNameType.UPDATE_PLAYER_PROPERTY_ALL)) {
				if (listeners.get(eventType) != null && listeners.get(eventType).size() > 0) {
					log.error("注册事件出现重复注册,原因:玩家信息变更事件已注册");
					return;
				}
			}
			if (listeners.get(eventType) == null) {
				Collection<ObjectListener> tempInfo = new HashSet<ObjectListener>();
				tempInfo.add(objectListener);
				listeners.put(eventType, tempInfo);
			} else {
				listeners.get(eventType).add(objectListener);
			}
			debugEventMsg("注册一个事件,类型为" + eventType);
		}
	}

	public void removeListener(ObjectListener objectListener, int eventType) {
		synchronized (lock) {
			if (listeners == null)
				return;
			Collection<ObjectListener> tempInfo = listeners.get(eventType);
			if (tempInfo != null) {
				tempInfo.remove(objectListener);
			}
		}
		debugEventMsg("移除一个事件,类型为" + eventType);
	}

	public void notifyListeners(ObjectEvent event) {
		List<ObjectListener> tempList = null;
		synchronized (lock) {
			if (listeners == null)
				return;
			int eventType = event.getEventType();
			if (listeners.get(eventType) != null) {
				Collection<ObjectListener> tempInfo = listeners.get(eventType);
				tempList = new ArrayList<ObjectListener>();
				Iterator<ObjectListener> iter = tempInfo.iterator();
				while (iter.hasNext()) {
					ObjectListener listener = (ObjectListener) iter.next();
					tempList.add(listener);
				}
			}
		}

		// 触发
		if (tempList != null) {
			for (ObjectListener listener : tempList) {
				if (listener == null) {
					log.error("", new Exception());
				}
				listener.onEvent(event);
			}
		}
	}

	public void clearListener() {
		synchronized (lock) {
			if (listeners != null) {
				listeners.clear();
				listeners = null;
			}
		}
	}

	public void debugEventMsg(String msg) {
		if (isDebug) {
			log.info(msg);
		}
	}
}

class AbstractEventLock extends Object {

}
