package com.app.empire.scene.service.battle.buffer.specialbuf;

import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.battle.buffer.FormulaBuffer;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.role.objects.Living;

/** 破甲 */
public class DefenceBreakBuffer extends FormulaBuffer {

	public DefenceBreakBuffer(Living source, Living target, SkillBufferTemplateInfo bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {

	}

	public int calculation(int initDefence, int param2) {
		SkillBufferTemplateInfo temp = getBufferInfo();
		int random = temp.getParam1();

		if (RND.next(10000) >= random) {
			return initDefence;
		}
		int result = initDefence * temp.getParam2() / 10000 + temp.getParam3();
		return result;
	}

}
