package com.app.empire.scene.constant;

public class DropItemConstant {

	public interface DropType{
		
		/** 机率掉落 */
		public static final byte probability = 1;
		
		/** 权重掉落 */
		public static final byte weight = 2;
	}
	
	public interface VisibleType{
		/**
		 * 只有自己看到
		 */
		public static final short privateDrop = 1;
		
		/**
		 * 所有人可见
		 */
		public static final short publicVisible = 2;
		
		/**
		 * 队内可见
		 */
		public static final short teamDrop = 3;
	}
	
	public interface notifyAction{
		/**
		 * 掉落物暴出的时候通知
		 */
		public static final short addDrop = 1;
		
		/**
		 * 玩家进地图或走近掉落物时同步给玩家已经存在的掉落物
		 */
		public static final short synchronizationDrop = 2;
	}
}
