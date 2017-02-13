package com.app.empire.world.common.event;

import java.util.EventObject;

public class ObjectEvent extends EventObject {

	private static final long serialVersionUID = -855454486771839444L;
	private Object objData;
	private int eventType;

	/**
	 * @param obj
	 *            系统默认参数
	 * @param objData
	 *            自定义参数
	 * @param eventType
	 *            事件健值
	 */
	public ObjectEvent(Object obj, Object objData, int eventType) {
		super(obj);
		this.objData = objData;
		this.eventType = eventType;
	}

	public void setObject(Object objData) {
		this.objData = objData;
	}

	public Object getObject() {
		return this.objData;
	}

	public int getEventType() {
		return eventType;
	}
}
