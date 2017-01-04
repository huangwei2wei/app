package com.app.empire.scene.service.battle.buffer;

public class BufferOverlayType {
	/** 啥也不干 */
	public static final int	NOTHING				= 0;

	/** 替换 */
	public static final int	REPLACE				= 1;

	/** 保留时间 长的 */
	public static final int	REPLACE_TIME		= 2;

	/** 保留次数多的 */
	public static final int	REPLACE_COUNT		= 3;

	/** 叠加 时间跟次数 */
	public static final int	SUPERIMPOSED		= 4;

	/** 叠加效果 */
	public static final int	SUPERIMPOSED_EFFECT	= 5;

}
