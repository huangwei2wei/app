package com.app.empire.scene.constant;

public class SceneGlobal {

	/**
	 * 怪物刷新时间
	 */
	public static final int	MONSTER_SPAWN_TIME			= 5000;

	/**
	 * 怪物AI计算频率
	 */
	public static final int	AI_MONSTER_DELAY			= 200;

	/**
	 * 受击时间
	 */
	public static final int	AI_BEATTACK_TIME			= 1000;

	/**
	 * 攻击者针对怪物脱战的时间
	 */
	public static final int	AI_MONSTER_OVERDUE			= 6000;

	/**
	 * 仇恨计算频率
	 */
	public static final int	AI_MONSTER_HETRED_RECOUNT	= 5000;

	/**
	 * 搜索敌对频率
	 */
	public static final int	AI_MONSTER_FIND_ENEMY		= 2000;

	/**
	 * 单次巡逻时间
	 */
	public static final int	AI_MONSTER_PATROL			= 5000;

	/**
	 * 空闲时间
	 */
	public static final int	AI_MONSTER_IDEL				= 200;

	/**
	 * 脱离追击范围
	 */
	public static final int	AI_MONSTER_OUTCHASE			= 15;

	/**
	 * 巡逻范围
	 */
	public static final int	AI_MONSTER_PATROLRANGE		= 3;
	/**
	 * 怪物默认攻击范围
	 */
	public static final int	AI_MONSTER_ATTACK_RANGE		= 2;
	/**
	 * 怪物攻击冷却时间
	 */
	public static final int	AI_MONSTER_ATTACK_COOL_DOWN	= 2000;
	/**
	 * 怪物体积半径
	 */
	public static final int	AI_MONSTER_RADIUS			= 1;
	/**
	 * 不主动攻击
	 */
	public static final int	AI_ACTIVEATTACK				= 0;
	/**
	 * 主动攻击玩家
	 */
	public static final int	AI_ACTIVEATTACK_PLAYER		= 1;
	/**
	 * 主动攻击怪物
	 */
	public static final int	AI_ACTIVEATTACK_MONSTER		= 2;

}
