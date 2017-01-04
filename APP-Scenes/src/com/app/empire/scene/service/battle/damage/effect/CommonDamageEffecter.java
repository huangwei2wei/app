package com.app.empire.scene.service.battle.damage.effect;

import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.damage.DamageType;
import com.app.empire.scene.service.role.objects.Living;

/** 普通伤害：【 扣血，优先气血，元魂态扣元魂】 */
public class CommonDamageEffecter implements DamageEffecter {

	@Override
	public Damage exec(Living target, Damage damage) {
		int value = damage.getDamageValue();
		// 当对象为元魂态时，直接扣元魂
		if (target.isSoulState()) {
			// 转换伤害类型
			damage.setDamageType(DamageType.CUR_SOUL);
		}
		// 初始元魂
		int oldValue = target.getProperty(damage.getDamageType());
		// 当前元魂
		int curValue = oldValue;
		// 扣除
		curValue -= value;
		if (curValue < 0) {
			curValue = 0;
			// 实际扣除元魂数
			value = oldValue - curValue;
		}
		damage.setDamageValue(value);
		EnumAttr attr = EnumAttr.getEnumAttrByValue(damage.getDamageType());
		// 赋值给living对象
		target.setProperty(attr, curValue);
		damage.setLeftValue(curValue);
		return damage;
	}

}
