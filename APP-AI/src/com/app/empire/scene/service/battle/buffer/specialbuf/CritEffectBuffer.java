package com.app.empire.scene.service.battle.buffer.specialbuf;

import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.battle.buffer.FormulaBuffer;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.role.objects.Living;

/** 影响暴击概率的buffer */
public class CritEffectBuffer extends FormulaBuffer {

	public CritEffectBuffer(Living source, Living target, SkillBufferTemplateInfo bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		Log.error("------------执行----------------");
	}

	public int calculation(int curBlood, int maxBlood) {
		if (curBlood <= maxBlood * bufferInfo.getParam1() / 10000) {
			return 1;
		} else {
			return 0;
		}
	}
}
