package com.app.empire.scene.service.battle.snare;

public class SnareConstant {
	// 生效时机
	public static class ExeWay {
		public static final int	IN				= 1;	// 进入时
		public static final int	TOUCH_PRE_TIME	= 2;	// 对陷阱对象定时执行
		public static final int	OUT				= 3;	// 对象离开时执行
	}

	// 作用对象
	public static class TargetType {
		public static final int	ENEMY			= 1;// 敌方
		public static final int	ENEMY_PLAYER	= 2;// 敌方，仅玩家
		public static final int	ENEMY_MONSTER	= 3;// 敌方，仅怪物
		public static final int	FRIENDLY		= 4;// 友方
		public static final int	ALL_OTHER		= 5;// 所有其他人
	}

	// 出生点类型
	public static class BornType {
		public static final int	SOURCE		= 1;// 以自己为中心
		public static final int	TARGET		= 2;// 以目标为中心
		public static final int	RELATIVELY	= 3;// 相对坐标点生成
	}

	// 陷阱移动类型
	public static class MoveType {
		public static final int	STANDING		= 1;	// 站立
		public static final int	MOVE_LINE		= 2;	// 直线移动
		public static final int	MOVE_LOCKING	= 3;	// 锁定目标
		public static final int	MOVE_RANDOM		= 4;	// 随机移动
	}

	// 陷阱锁定模式
	public static class LockType {
		public static final int	NONE			= 1;	// 不锁定
		public static final int	FIRST_TARGET	= 2;	// 首选目标
		public static final int	FIRST_BE_ATTACK	= 3;	// 首选被攻击目标
		public static final int	ATTACKER		= 4;	// 攻击者
	}
}
