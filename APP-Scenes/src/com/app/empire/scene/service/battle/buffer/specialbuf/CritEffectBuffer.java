package com.app.empire.scene.service.battle.buffer.specialbuf;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.FormulaBuffer;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.role.objects.Living;

/** 影响暴击概率的buffer */
public class CritEffectBuffer extends FormulaBuffer {

	public CritEffectBuffer(Living source, Living target, SkillBuffer bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		System.err.print("------------执行----------------");
	}

	public int calculation(int curBlood, int maxBlood) {
		if (curBlood <= maxBlood * bufferInfo.getParam1() / 10000) {
			return 1;
		} else {
			return 0;
		}
	}
}
