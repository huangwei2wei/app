package com.app.empire.scene.service.battle.damage;

import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.util.ThreadSafeRandom;

/**
 * 伤害计算接口
 *
 */
public interface DamageCalculator {
	ThreadSafeRandom random = new ThreadSafeRandom();

	public int calcDamage(Living source, Living target, int parent, int value);

}
