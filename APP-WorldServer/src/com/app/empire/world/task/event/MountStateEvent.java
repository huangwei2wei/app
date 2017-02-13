package com.app.empire.world.task.event;

import com.chuangyou.xianni.event.ObjectEvent;

public class MountStateEvent extends ObjectEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int targetId;
	private int targetId1;
	private int targetNum;
	
	
	
	public MountStateEvent(Object obj, int targetId, int targetId1, int targetNum,int eventType) {
		super(obj, null, eventType);
		this.targetId = targetId;
		this.targetId1 = targetId1;
		this.targetNum = targetNum;
		
	
	}


	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public int getTargetNum() {
		return targetNum;
	}

	public void setTargetNum(int targetNum) {
		this.targetNum = targetNum;
	}


	public int getTargetId1() {
		return targetId1;
	}


	public void setTargetId1(int targetId1) {
		this.targetId1 = targetId1;
	}


}
