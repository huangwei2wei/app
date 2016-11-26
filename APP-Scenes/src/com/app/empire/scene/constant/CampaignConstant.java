package com.app.empire.scene.constant;

//副本操作
public class CampaignConstant {

	// ----------------副本操作--------------------//
	/** 获取信息 */
	public static final int	GET_INFO	= 1;
	/** 进入 */
	public static final int	JOIN		= 2;
	/** 退出 */
	public static final int	LEAVE		= 3;
	/** 成员进入队伍所在副本 */
	public static final int	JOIN_TEAM	= 4;

	// ----------------scene副本通知center状态--------------------//
	public static class CampaignStatu {
		public static final int	NOTITY2C_OUT			= 0;	// 退出
		public static final int	NOTITY2C_IN				= 1;	// 进入
		public static final int	NOTITY2C_OUT_SUCCESS	= 2;	// 成功结束退出
		public static final int	NOTITY2C_OUT_FAIL		= 3;	// 失败结束退出
		public static final int	NOTITY2C_SUCCESS		= 4;	// 副本成功结算
	}

	public static class CampaignType {
		public static final int	SINGLE		= 1;	// 单人本
		public static final int	TEAM		= 2;	// 组队副本
		public static final int	BEAD		= 3;	// 天逆珠副本
		public static final int	CHALLENG	= 4;	// 挑战副本
	}

}
