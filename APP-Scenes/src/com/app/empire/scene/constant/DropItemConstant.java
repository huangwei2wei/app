package com.app.empire.scene.constant;

public class DropItemConstant {

	public interface DropType{
		
		/** 机率掉落 */
		public static final byte PROBABILITY = 1;
		
		/** 权重掉落 */
		public static final byte WEIGHT = 2;
		
		/** 特殊权重算法掉落,权重总值固定 */
		public static final byte SPECIAL_WEIGHT = 3;
	}
	
	public interface VisibleType{
		/**
		 * 只有自己看到
		 */
		public static final short PRIVATEDROP = 1;
		
		/**
		 * 所有人可见
		 */
		public static final short PUBLICVISIBLE = 2;
		
		/**
		 * 队内可见
		 */
		public static final short TEAMDROP = 3;
	}
	
	public interface notifyAction{
		/**
		 * 掉落物暴出的时候通知
		 */
		public static final short ADDDROP = 1;
		
		/**
		 * 玩家进地图或走近掉落物时同步给玩家已经存在的掉落物
		 */
		public static final short SYNCHRONIZATIONDROP = 2;
	}
}
