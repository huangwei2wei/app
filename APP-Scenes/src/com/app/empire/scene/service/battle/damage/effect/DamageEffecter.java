package com.app.empire.scene.service.battle.damage.effect;

import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.role.objects.Living;

/** 伤害效果 */
public interface DamageEffecter {
	public Damage exec(Living target, Damage damage);
}
