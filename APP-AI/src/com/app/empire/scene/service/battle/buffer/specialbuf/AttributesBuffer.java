package com.app.empire.scene.service.battle.buffer.specialbuf;

import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.battle.buffer.Buffer;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.role.objects.Living;

/** 属性类型buffer */
public class AttributesBuffer extends Buffer {

	public AttributesBuffer(Living source, Living target, SkillBufferTemplateInfo bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		if (getBufferInfo().getValueType() != 0) {
			source.refreshProperties(getBufferInfo().getValueType());
		}

		if (getBufferInfo().getValueType1() != 0) {
			source.refreshProperties(getBufferInfo().getValueType1());
		}
	}

}
