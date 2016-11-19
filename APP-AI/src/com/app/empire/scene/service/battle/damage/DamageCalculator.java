package com.app.empire.scene.service.battle.damage;

import com.chuangyou.common.util.ThreadSafeRandom;
import com.chuangyou.xianni.entity.skill.SkillActionTemplateInfo;
import com.chuangyou.xianni.role.objects.Living;

/**
 * 伤害计算接口
 *
 */
public interface DamageCalculator {
	ThreadSafeRandom random = new ThreadSafeRandom();

	public int calcDamage(Living source, Living target, int parent, int value);

}
