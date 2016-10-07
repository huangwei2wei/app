package com.app.empire.scene.service.battle.buffer;

/**
 * 执行时机
 */
public class ExecWayType {
	/** 时间轴 */
	public static final int	TIME_LINE	= 1;

	/** 攻击时 */
	public static final int	ATTACT		= 2;

	/** BUFFER移除 */
	public static final int	REMOVE		= 3;

	/** 受伤时 */
	public static final int	HURT		= 4;

	/** 添加时 */
	public static final int	ADD			= 5;

	/** 魂幡触发 */
	public static final int	FUSE		= 6;

}
