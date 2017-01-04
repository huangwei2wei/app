package com.app.empire.scene.constant;

//副本操作
public class CampaignConstant {

	// ----------------副本操作--------------------//
	/** 获取信息 */
	public static final int GET_INFO = 1;
	/** 进入 */
	public static final int JOIN = 2;
	/** 退出 */
	public static final int LEAVE = 3;
	/** 成员进入队伍所在副本 */
	public static final int JOIN_TEAM = 4;
	/** 清除副本 */
	public static final int DETORY = 5;

	/** 副本相关内容中需要触发脚本的id */
	public static final String SCRIPTID = "campaign";

	// ----------------scene副本通知center状态--------------------//
	public static class CampaignStatu {
		public static final int NOTITY2C_OUT = 0; // 退出
		public static final int NOTITY2C_IN = 1; // 进入
		public static final int NOTITY2C_SUCCESS = 2; // 成功结束退出
		public static final int NOTITY2C_OVER = 3; // 失败结束退出
		// public static final int NOTITY2C_SUCCESS = 4; // 副本成功结算
	}

	public static class CampaignType {
		public static final int SINGLE = 1; // 单人本
		public static final int TEAM = 2; // 组队副本
		public static final int BEAD = 3; // 天逆珠副本
		public static final int AVATAR = 4; // 挑战副本(更换为分身副本)
		public static final int ARENA = 5; // 竞技场副本
		public static final int STATE = 6; // 境界副本
		public static final int PVP_1V1 = 7; // PVP11副本
		public static final int GUILD_SEIZE = 8; // 帮派夺权副本
		public static final int ELITE_BOSS_TRIGGER = 9; // 精英BOSS触发副本
		public static final int NPC_CAM_ACTIVITY = 10; // 定点进本活动副本
		public static final int WORLD_BOSS_TRIGGER = 11; // 世界BOSS触发副本
		public static final int THE_PLANE = 12; // 位面副本
		public static final int PLOT = 13; // 剧情副本
	}

	public static class ChallengeResult {
		/** 输 */
		public static final int FAIL = 0;
		/** 赢 */
		public static final int WIN = 1;

		/** 挑战未开始，玩家已经在副本中，不能开始挑战 */
		public static final int ALREADY_IN_CAMPAIGN = 2;

		/** 挑战未开始，未知错误 */
		public static final int START_FAIL = 3;
	}

}
