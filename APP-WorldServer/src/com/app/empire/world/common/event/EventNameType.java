package com.app.empire.world.common.event;

/**
 * 事件类型
 * @author doter
 *
 */
public interface EventNameType {
	/**金币改变*
	public static final int	MONEY_CHANGE			= 1;
	
	/**所有属性变更*/ 
	public static final int	UPDATE_PLAYER_PROPERTY_ALL	= 2;
	
	/** 玩家属性变更 */
	public static final int UPDATE_PLAYER_PROPERTY = 5;
	
	/**
	 * 杀怪任务
	 */
	public static final int TASK_KILL_MONSTER   = 6;
	/**  采集任务   */
	public static final int TASK_PATCH    = 7;
	/**  通关副本   */
	public static final int TASK_PASS_FB = 8;
	/**  物品改变 添加  */
	public static final int TASK_ITEM_CHANGE_ADD = 9;
	/** npc对话 */
	public static final int TASK_NPC_DIALOG = 10;
	/**  物品改变  减少 */
	public static final int TASK_ITEM_CHANGE_REDUCE = 11;
	/** QTE操作完成   */
	public static final int TASK_QTE   = 12;
	/** 触发性任务事件   */
	public static final int TASK_TRIGGER = 13;
	/** 天珠系统 */
	public static final int TASK_T_SYSTEM = 14;
	
	/** 添加组员  */
	public static final int TEAM_ADD_MEMBER = 14;
	/** 移出组员   */
	public static final int TEAM_REMOVE_MEMBER = 15;
	/** 队伍满  */
	public static final int TEAM_IS_FULL  = 16;
	/** 队伍已空  */
	public static final int TEAM_IS_EMPTY = 17;
	/** 队长改变  */
	public static final int TEAM_LEADER_CHANGE = 18;
	/** 队伍都离线 */
	public static final int TEAM_IS_ALLOFFLINE = 19;
	/** 队员在线与离线状态发生改变   */
	public static final int TEAM_CHNAGE_ONLINE = 20;
	/**
	 *  主魂等级变更
	 */
	public static final int SOUL_LV  = 21;
	/**
	 *  装备
	 */
	public static final int EQUIP  = 22;
	/**
	 *  主魂星级
	 */
	public static final int SOUL_STAR = 23;
	/**
	 * 制作魂幡熟练度
	 */
	public static final int SOUL_PRO  = 24;
	/**
	 *  坐骑
	 */
	public static final int MOUNT  = 25;
	/**
	 * 神器
	 */
	public static final int ARTIFACT = 26;
	/**
	 * 法宝激活
	 */
	public static final int MAGICWP_ACTIVE = 27;
	/**
	 * 法宝
	 */
	public static final int MAGICWP = 28;
	/**
	 * 宠物
	 */
	public static final int PET_ACTIVE = 29;
	/**
	 * 宠物
	 */
	public static final int PET = 30;
	/**
	 * 魂幡战斗力
	 */
	public static final int SOUL_FIGHT = 31;
	/**
	 * 法宝战力
	 */
	public static final int MAGICWP_FIGHT = 32;
	
	/**
	 * 宠物战力
	 */
	public static final int PET_FIGHT     = 33;
	
	/**
	 * 完成任务，添加活跃值
	 */
	public static final int ADD_ACTIVE_VALUE = 34;
	
	/**
	 * 境界任务完成
	 */
	public static final int STATE_TAKS_FINISH = 35;
	
	/**
	 * 技能相关的事件
	 */
	public static final int SKILL_LEVEL  = 36;
	
	/**
	 * 分身数据更新事件
	 */
	public static final int AVATAR_UPDATE = 37;
}
