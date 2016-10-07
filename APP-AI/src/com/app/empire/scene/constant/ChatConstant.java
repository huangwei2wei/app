package com.app.empire.scene.constant;

public class ChatConstant {

	/**
	 * 频道类型
	 * @author Gxf
	 *
	 */
	public static interface Channel{
		/**
		 * 系统频道
		 */
		public static final int SYSTEM = 1;
		
		/**
		 * 世界频道
		 */
		public static final int WORLD = 2;
		
		/**
		 * 地图频道
		 */
		public static final int SCENE = 3;
		
		/**
		 * 帮派频道
		 */
		public static final int FACTION = 4;
		
		/**
		 * 队伍频道
		 */
		public static final int TEAM = 5;
		
		/**
		 * 组队频道
		 */
		public static final int SEARCH_TEAM = 6;
		
		/**
		 * 私有频道 一对一聊天
		 */
		public static final int PRIVATE = 7;
		
		/**
		 * 系统提示频道
		 */
		public static final int PROMPT = 8;
	}
	
	/**
	 * 各频道发言CD时间(单位：毫秒)
	 * @author Gxf
	 *
	 */
	public static interface CoolingTime{
//		/**
//		 * 系统频道
//		 */
//		public static final int SYSTEM = 0;
//		
//		/**
//		 * 世界频道
//		 */
//		public static final int WORLD = 45000;
//		
//		/**
//		 * 地图频道
//		 */
//		public static final int SCENE = 30000;
//		
//		/**
//		 * 帮派频道
//		 */
//		public static final int FACTION = 10000;
//		
//		/**
//		 * 队伍频道
//		 */
//		public static final int TEAM = 5000;
//		
//		/**
//		 * 组队频道
//		 */
//		public static final int SEARCH_TEAM = 20000;
		
		/**
		 * 系统频道
		 */
		public static final int SYSTEM = 0;
		
		/**
		 * 世界频道
		 */
		public static final int WORLD = 2000;
		
		/**
		 * 地图频道
		 */
		public static final int SCENE = 2000;
		
		/**
		 * 帮派频道
		 */
		public static final int FACTION = 2000;
		
		/**
		 * 队伍频道
		 */
		public static final int TEAM = 2000;
		
		/**
		 * 组队频道
		 */
		public static final int SEARCH_TEAM = 2000;
		
		/**
		 * 私有频道
		 */
		public static final int PRIVATE = 0;
	}
	
	public static interface PromptType{
		
		/**
		 * 普通文字提示
		 */
		public static final short COMMON = 1;
		
		/**
		 * 添加好友提示
		 */
		public static final short ADDFRIEND = 2;
	}
}
