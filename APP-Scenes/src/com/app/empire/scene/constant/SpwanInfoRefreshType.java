package com.app.empire.scene.constant;

public class SpwanInfoRefreshType {

	// 开始结束时间管理
	public static class SpwanInfoRangeType {
		public static final int	BORN_DIE	= 1;// 管理开始跟结束
		public static final int	ONLY_BORN	= 2;// 仅管理开始
	}

	// 刷新间隔管理
	public static class SpwanInfoIntervalType {
		public static final int	BRON_SIGN	= 1;// 以结束时间计时
		public static final int	DIE_SIGN	= 2;// 以刷新点为时间计时
	}
}
