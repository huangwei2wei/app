package com.app.empire.scene.service.battle.damage.effect;

import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.battle.damage.DamageType;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.role.objects.Living;

/** 【加血，先提升元魂，再提升气血】 */
public class CommonRestoreEffecter implements DamageEffecter {

	@Override
	public Damage exec(Living target, Damage damage) {
		int value = damage.getDamageValue();
		// 当前缺失的元魂
		int lessSoul = target.lessSoul();
		// 当前缺失气血
		int lessBlood = target.lessBlood();
		if (lessSoul > 0) {
			damage.setDamageType(DamageType.CUR_SOUL);
			value = Math.max(0 - lessSoul, value);
		} else if (lessBlood > 0) {
			damage.setDamageType(DamageType.CUR_BLOOD);
			value = Math.max(0 - lessBlood, value);
		} else {
			value = 0;
		}
		damage.setDamageValue(value);
		// 初始值
		int oldValue = target.getProperty(damage.getDamageType());
		EnumAttr attr = EnumAttr.getEnumAttrByValue(damage.getDamageType());
		// 赋值给living对象
		int curValue = oldValue - value;
		target.setProperty(attr, curValue);
		damage.setLeftValue(curValue);
		return damage;
	}

}
