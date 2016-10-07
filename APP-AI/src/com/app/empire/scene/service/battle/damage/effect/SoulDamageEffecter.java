package com.app.empire.scene.service.battle.damage.effect;

import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.role.objects.Living;

public class SoulDamageEffecter implements DamageEffecter {

	@Override
	public Damage exec(Living target, Damage damage) {
		int value = damage.getDamageValue();
		if (damage.getDamageType() != EnumAttr.CUR_SOUL.getValue()) {
			Log.error("battle error , blood only damage type is not cur_blood,skillId:" + damage.getFromType() + " ---" + damage.getFromId());
			damage.setDamageType(EnumAttr.CUR_SOUL.getValue());
		}
		// 初始气血
		int oldValue = target.getProperty(damage.getDamageType());
		// 当前气血
		int curValue = oldValue;
		// 扣除
		curValue -= value;
		if (curValue < 0) {
			curValue = 0;
			// 实际扣除
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
