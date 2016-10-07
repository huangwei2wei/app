package com.app.empire.scene.service.battle.damage;

import java.util.List;

import com.chuangyou.xianni.battle.buffer.Buffer;
import com.chuangyou.xianni.battle.buffer.BufferType;
import com.chuangyou.xianni.battle.buffer.FormulaBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.BeAttackDamageEffectBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.DefenceBreakBuffer;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.role.objects.Living;

/**
 * 气血伤害 计算公式： （max（攻击-对方防御*1.2，0）+攻击*0.025）*random（0.8,1.2）
 */
public class BloodDamageCalculator implements DamageCalculator {

	@Override
	public int calcDamage(Living source, Living target, int percent, int value) {
		int attack = source.getAttack();
		int defence = target.getDefence();
		// 获取所有破甲buffer
		List<Buffer> defenceBreakBuffers = source.getTypeBuffers(BufferType.DEFENCE_BREAK);
		int dec = 0;
		for (Buffer buff : defenceBreakBuffers) {
			FormulaBuffer debuff = (FormulaBuffer) buff;
			dec += debuff.formulaExe(defence, 0);
		}
		// 获得计算的护甲
		defence -= dec;
		int damageValue = (int) ((Math.max(attack - defence * 1.2, 0) + attack * 0.025) * random.next(80, 120) / 100);
		damageValue = (int) (1l * damageValue * percent / 10000 + value);

		// 伤害实际值,受源与目标buffer状态修正
		int changeValue = 0;
		// 伤害源修正
		List<Buffer> sourceDmageEffBuff = source.getTypeBuffers(BufferType.CASTER_DAMAGE_EFFECT);
		for (Buffer buff : sourceDmageEffBuff) {
			FormulaBuffer fbuff = (FormulaBuffer) buff;
			changeValue += fbuff.formulaExe(damageValue, EnumAttr.BLOOD.getValue());
		}
		// 伤害目标修正
		List<Buffer> targetDmageEffBuff = target.getTypeBuffers(BufferType.BE_ATTACK_DAMAGE_EFFECT);
		for (Buffer buff : targetDmageEffBuff) {
			FormulaBuffer fbuff = (FormulaBuffer) buff;
			changeValue += fbuff.formulaExe(damageValue, EnumAttr.BLOOD.getValue());
		}
		damageValue = damageValue - changeValue;
		if (percent == 0 && value == 0) {
			return damageValue;
		}
		return damageValue == 0 ? 1 : damageValue;
	}

}
