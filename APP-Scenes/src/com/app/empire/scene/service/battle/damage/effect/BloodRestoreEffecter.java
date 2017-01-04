package com.app.empire.scene.service.battle.damage.effect;

import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;

import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.damage.DamageType;
import com.app.empire.scene.service.role.objects.Living;

/** 只恢复气血 */
public class BloodRestoreEffecter implements DamageEffecter {
	protected Logger log = Logger.getLogger(DamageEffecter.class);
	@Override
	public Damage exec(Living target, Damage damage) {
		int value = damage.getDamageValue();

		if (damage.getDamageType() != EnumAttr.CUR_BLOOD.getValue()) {
			log.error("battle error , blood only damage type is not cur_blood,skillId:" + damage.getFromType() + " ---" + damage.getFromId());
			damage.setDamageType(EnumAttr.CUR_BLOOD.getValue());
		}

		// 当前缺失气血
		int lessBlood = target.lessBlood();
		if (lessBlood > 0) {
			damage.setDamageType(DamageType.CUR_BLOOD);
			value = Math.max(0 - lessBlood, value);
		} else {
			value = 0;
		}
		damage.setDamageValue(value);
		// 初始气血
		int oldValue = target.getProperty(damage.getDamageType());
		EnumAttr attr = EnumAttr.getEnumAttrByValue(damage.getDamageType());
		// 赋值给living对象
		int curValue = oldValue - value;
		target.setProperty(attr, curValue);
		damage.setLeftValue(curValue);
		return damage;
	}

}
