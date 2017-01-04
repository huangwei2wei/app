package com.app.empire.scene.service.battle.buffer.specialbuf;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.FormulaBuffer;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.role.objects.Living;

/** 伤害修改buffer */
public class CasterDamageEffectBuffer extends FormulaBuffer {

	public CasterDamageEffectBuffer(Living source, Living target, SkillBuffer bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {

	}

	/**
	 * @param damageValue
	 *            伤害值
	 * @param type
	 *            类型 0 通用,1 气血 2 元魂
	 */
	public int calculation(int damageValue, int type) {
		SkillBuffer temp = getBufferInfo();
		if (temp.getParam1() != 0 && temp.getParam1() != type) {
			return 0;
		}
		// 影响多少伤害
		int result = damageValue * temp.getParam2() / 10000 + temp.getParam3();
		// 叠加层数
		result = result * pressedNum;
		return result;
	}

}
