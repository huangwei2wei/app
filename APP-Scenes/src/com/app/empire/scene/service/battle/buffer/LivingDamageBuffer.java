package com.app.empire.scene.service.battle.buffer;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.damage.BloodDamageCalculator;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.damage.SoulDamageCalculator;
import com.app.empire.scene.service.role.objects.Living;

/** 伤害类型buffer */
public class LivingDamageBuffer extends Buffer {

	protected LivingDamageBuffer(Living source, Living target, SkillBuffer bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		int damageValue1 = 0;
		int type1 = bufferInfo.getValueType();

		if (type1 > 0) {
			if (type1 == EnumAttr.CUR_BLOOD.getValue()) {
				damageValue1 = calBlood(bufferInfo.getValuePercent(), bufferInfo.getValue());
			}
			if (type1 == EnumAttr.CUR_SOUL.getValue()) {
				damageValue1 = calSoul(bufferInfo.getValuePercent(), bufferInfo.getValue());
			}
			damageValue1 = calSoullv(damageValue1, 2);
			beDamage1.setTarget(target);
			beDamage1.setSource(source);
			beDamage1.setFromType(Damage.BUFFER);
			beDamage1.setFromId(this.getBufferId());
			beDamage1.setDamageType(type1);
			beDamage1.setDamageValue(damageValue1);
			beDamage1.setCalcType(getDamageType());
		}

		int type2 = bufferInfo.getValueType1();
		int damageValue2 = 0;
		if (type2 > 0) {
			if (type2 == EnumAttr.CUR_BLOOD.getValue()) {
				damageValue2 = calBlood(bufferInfo.getValuePercent1(), bufferInfo.getValue1());
			}
			if (type2 == EnumAttr.CUR_SOUL.getValue()) {
				damageValue2 = calSoul(bufferInfo.getValuePercent1(), bufferInfo.getValue1());
			}
			damageValue2 = calSoullv(damageValue2, 2);
			beDamage2.setFromType(Damage.BUFFER);
			beDamage2.setFromId(this.getBufferId());
			beDamage2.setTarget(target);
			beDamage2.setSource(source);
			beDamage2.setDamageType(type2);
			beDamage2.setDamageValue(damageValue2);
			beDamage2.setCalcType(getDamageType());
		}
	}

	// 向上取整{max（攻击-对方防御*1.2，攻击*0.025）*random（0.8,1.2）*MAX[1+（破血-对方血抗）/10000，0.1]}
	private int calBlood(int percent, int value) {
		return new BloodDamageCalculator().calcDamage(source, target, percent, value);
	}

	// 向上取整{max（魂攻-对方魂防*1.2，0）*random（0.7,1.3）*MAX[1+（破魂-对方魂抗）/10000，0.1]}
	private int calSoul(int percent, int value) {
		return new SoulDamageCalculator().calcDamage(source, target, percent, value);
	}

}
