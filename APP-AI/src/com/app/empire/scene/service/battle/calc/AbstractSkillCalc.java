package com.app.empire.scene.service.battle.calc;

import com.chuangyou.common.util.ThreadSafeRandom;
import com.chuangyou.xianni.battle.damage.DamageCalculator;

/**
 * 抽象技能计算工具
 * 
 */
public abstract class AbstractSkillCalc implements SkillCalc {

	protected static ThreadSafeRandom	random	= new ThreadSafeRandom();
	protected DamageCalculator			bloodCalcutor;
	protected DamageCalculator			soulCalcutor;
	protected int						orderId;

	public AbstractSkillCalc(int order, DamageCalculator bloodCalcutor, DamageCalculator soulCalcutor) {
		this.orderId = order;
		this.bloodCalcutor = bloodCalcutor;
		this.soulCalcutor = soulCalcutor;
	}

	/**
	 * 获取暴击增加的百分比 暴击伤害 基础伤害*max（（1.5+自身暴击伤害-对方抗暴减伤），1.2）
	 */
	public float getCoefficient(int critAddtion, int critCut) {
		float f = (float) Math.max(1.5 + (critAddtion - critCut) / 100f, 1.2);
		return f;
	}

	/**
	 * 是否暴击 （暴击-对方抗爆）*0.0001∈[5%，75%]
	 */
	protected boolean isCrit(int isCrit, int crit, int critDefence) {
		if (isCrit == 0) {
			return false;
		}
		int probability = (int) Math.min(Math.max(crit - critDefence, 500f), 7500);
		if (random.next(10000) <= probability) {
			return true;
		}
		return false;
	}

	/**
	 * 是否miss 闪避公式: 1+（命中-对方闪避）*0.0001∈[25%，100%]
	 */
	protected boolean isHit(int accurate, int dodge) {
		int probability = Math.min(Math.max(10000 + (accurate - dodge), 2500), 10000);
		if (random.next(10000) <= probability) {
			return true;
		}
		return false;
	}
}
