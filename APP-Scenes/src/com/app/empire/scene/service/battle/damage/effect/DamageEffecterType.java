package com.app.empire.scene.service.battle.damage.effect;

public interface DamageEffecterType {
	/** 普通伤害：【 扣血，优先气血，元魂态扣元魂】【加血，先提升元魂，再提升气血】 */
	public static final int	COMMON	= 1;
	/** 元魂伤害：【 扣血,只扣元魂】【加血，只加元魂】 */
	public static final int	SOUL	= 2;
	/** 气血伤害：【 扣血,只扣气血】【加血，只加元魂】 */
	public static final int	BLOOD	= 3;
}
