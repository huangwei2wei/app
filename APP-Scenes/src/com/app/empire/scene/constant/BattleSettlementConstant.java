package com.app.empire.scene.constant;

public class BattleSettlementConstant {
	// 击杀
	public static final int	KILLER	= 1;
	// 参与
	public static final int	JOINER	= 2;


	public static interface MonsterDropType {
		/** 仅击杀 */
		int	ONLY_KILLER		= 1;
		/** 所有参与者 */
		int	ERVERYBODY		= 2;
		/** 参与者仅获得经验 */
		int	JOINER_GET_EXP	= 3;
	}
}
