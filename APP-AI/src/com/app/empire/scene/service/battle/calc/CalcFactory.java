package com.app.empire.scene.service.battle.calc;

import com.chuangyou.xianni.battle.damage.DamageCalculator;
import com.chuangyou.xianni.battle.damage.DamageFactory;
import com.chuangyou.xianni.constant.EnumAttr;

public class CalcFactory {

	/**
	 * 固定技能，固定方式计算
	 * 
	 * @param skillId
	 * @param calcWay
	 * @return
	 */
	public static SkillCalc createSkillCalc(int skillId) {
		DamageCalculator bloodCalc = DamageFactory.createCalculator(EnumAttr.CUR_BLOOD);
		DamageCalculator soulCalc = DamageFactory.createCalculator(EnumAttr.CUR_SOUL);
		// if (bloodCalc == null) {
		// return null;
		// }
		SkillCalc calc = null;
		switch (skillId) {
			case 1:
			default:
				calc = new SingleLivingAttack(skillId, bloodCalc, soulCalc);
				break;
		}
		return calc;
	}

	/**
	 * 固定技能，固定伤害计算
	 * 
	 * @param skillId
	 * @param fixDamage
	 * @return
	 */
	/*
	 * public static SkillCalc createFixDamageCalc(int skillId, int fixDamage) {
	 * DamageCalculator damageCalc = new FixedDamage(fixDamage); if(damageCalc
	 * == null) { return null; }
	 * 
	 * SkillCalc calc = new SingleLivingAttack(skillId, damageCalc); return
	 * calc; }
	 */
}
