package com.app.empire.scene.service.battle.buffer;

/**buffer 耐久*/
public class DurableType {
	/**
	 * 时间减少
	 */
	public static final int TIME = 1;
	
	/**
	 * 按作用次数减少
	 */
	public static final int COUNT = 2;
	
	/**
	 * 按作用次数减少和时间同时控制
	 */
	public static final int TIME_AND_COUNT = 3;
}
