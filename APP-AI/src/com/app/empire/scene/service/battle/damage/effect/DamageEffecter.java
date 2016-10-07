package com.app.empire.scene.service.battle.damage.effect;

import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.role.objects.Living;

/**伤害效果*/
public interface DamageEffecter {
	public Damage exec(Living target, Damage damage);
}
