package com.app.empire.scene.service.battle.damage;

import com.app.empire.scene.constant.EnumAttr;

public class DamageFactory {

	public static DamageCalculator createCalculator(EnumAttr type) {
		DamageCalculator calculator = null;
		// EnumAttr type = EnumAttr.getEnumAttrByValue(calcWay);
		// if (type == null) {
		// Log.error("传入计算方式有误, 检查配置; calcWay1: " + calcWay);
		// return new BloodDamageCalculator();
		// }
		switch (type) {
			case CUR_SOUL:
				calculator = new SoulDamageCalculator();
				break;
			case CUR_BLOOD:
				calculator = new BloodDamageCalculator();
				break;
			default:
				calculator = new BloodDamageCalculator();
				// Log.error("传入计算方式有误, 检查配置; calcWay: " + calcWay);
				break;
		}
		return calculator;
	}
}
