package com.app.empire.scene.service.battle.buffer.specialbuf;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.FormulaBuffer;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.role.objects.Living;

public class SoulDefenceBreakBuffer extends FormulaBuffer {

	public SoulDefenceBreakBuffer(Living source, Living target, SkillBuffer bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {

	}

	public int calculation(int initSoulDefence, int param2) {
		SkillBuffer temp = getBufferInfo();
		int random = temp.getParam1();

		if (RND.next(10000) >= random) {
			return initSoulDefence;
		}
		int result = initSoulDefence * temp.getParam2() / 10000 + temp.getParam3();
		return result;
	}

}
