package com.app.empire.scene.service.battle.damage;

import java.util.List;

import com.chuangyou.xianni.battle.buffer.Buffer;
import com.chuangyou.xianni.battle.buffer.BufferType;
import com.chuangyou.xianni.battle.buffer.FormulaBuffer;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.role.objects.Living;

/**
 * 魂攻伤害 计算公式： max（魂攻-对方魂防*1.2，0）*random（0.7,1.3）
 */
public class SoulDamageCalculator implements DamageCalculator {

	@Override
	public int calcDamage(Living source, Living target, int percent, int value) {
		// 已方魂攻
		int soulAttack = source.getSoulAttack();
		// 对方魂防
		int soulDeffence = target.getSoulDefence();

		// 获取所有破甲buffer
		List<Buffer> defenceBreakBuffers = source.getTypeBuffers(BufferType.SOUL_DEFECT_BREAK);
		int dec = 0;
		for (Buffer buff : defenceBreakBuffers) {
			FormulaBuffer debuff = (FormulaBuffer) buff;
			dec += debuff.formulaExe(soulDeffence, 0);
		}
		// 获得计算的护甲
		soulDeffence -= dec;

		// 当对方处于元魂状态时，对方魂防御降低50%
		if (target.isSoulState()) {
			soulDeffence -= soulDeffence * 0.3f;
		}
		int damageValue = (int) (Math.max(soulAttack - soulDeffence * 1.2, 0) * random.next(70, 130) / 100);
		damageValue = (int) (1l * damageValue * percent / 10000 + value);

		// 伤害实际值,受源与目标buffer状态修正
		int changeValue = 0;
		// 伤害源修正
		List<Buffer> sourceDmageEffBuff = source.getTypeBuffers(BufferType.CASTER_DAMAGE_EFFECT);
		for (Buffer buff : sourceDmageEffBuff) {
			FormulaBuffer fbuff = (FormulaBuffer) buff;
			changeValue += fbuff.formulaExe(damageValue, EnumAttr.SOUL.getValue());
		}
		// 伤害目标修正
		List<Buffer> targetDmageEffBuff = target.getTypeBuffers(BufferType.BE_ATTACK_DAMAGE_EFFECT);
		for (Buffer buff : targetDmageEffBuff) {
			FormulaBuffer fbuff = (FormulaBuffer) buff;
			changeValue += fbuff.formulaExe(damageValue, EnumAttr.SOUL.getValue());
		}
		if (percent == 0 && value == 0) {
			return damageValue;
		}
		damageValue = damageValue - changeValue;
		return damageValue == 0 ? 1 : damageValue;
	}

}
