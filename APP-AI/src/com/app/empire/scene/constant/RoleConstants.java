package com.app.empire.scene.constant;

public class RoleConstants {
	public interface RoleType {
		int	player		= 1;	// 玩家
		int	monster		= 2;	// 怪物
		int	pet			= 3;	// 宠物
		int	npc			= 4;	// NPC
		int	gather		= 5;	// 采集物
		int	snare		= 6;	// 陷阱
		int	truck		= 7;	// 镖车
		int	robot		= 8;	// 机器人
		int	matrial		= 9;	// 物资
		int	avatar		= 10;	// 分身
		int	transfer	= 11;	// 传送点
		int	plot		= 12;	// 剧情NPC
	}

	/**
	 * 0:不需要处理 1:总数显示 2:需要判断运镖时间和镖车关系 (在运镖时间不显示，和自己无关的运镖不显示) 3:需要判断运镖时间
	 * (在运镖时间不显示) 4:需要判断玩家和主人运镖时间和镖车关系
	 */
	public static final int[][] TruckTimerRelation = new int[][] {
			// player monster pet npc gather snare truck robot,matrial,avatar,transfer,plot
			{ 2, 3, 4, 1, 4, 4, 1, 1, 2, 1, 1, 1 }, // player
			{ 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // monster
			{ 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // pet
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // npc
			{ 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // gather
			{ 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // snare
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // truck
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // robot
			{ 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // matrial
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // avatar
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // transfer
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }  // plot
	};

	/**
	 * 怪物类型
	 *
	 */
	public interface MonsterType {

		/** 普通怪 */
		public static final int	COMMON		= 1;

		/** 精英BOSS */
		public static final int	ELITE_BOSS	= 2;

		/** 世界BOSS */
		public static final int	WORLD_BOSS	= 3;
	}

	/**
	 * NPC类型
	 * 
	 * @author Joseph
	 *
	 */
	public interface NpcType {

		/** 正常NPC */
		public static final int	NPC					= 2;

		/** 传送点 */
		public static final int	TRANSFER			= 3;

		/** 采集物 */
		public static final int	GATHER				= 4;

		/** 触发器 */
		public static final int	TRIGGER				= 5;

		/** 副本触发器 */
		public static final int	CAMPAIGNPOINT		= 6;

		/** 动态传送点 */
		public static final int	DYNAMIC_TRANSFER	= 7;
	}
}
