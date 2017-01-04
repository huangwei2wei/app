package com.app.empire.scene.service.battle.buffer.specialbuf;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.role.objects.Living;

/** 属性类型buffer */
public class AttributesBuffer extends Buffer {

	public AttributesBuffer(Living source, Living target, SkillBuffer bufferInfo) {
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
