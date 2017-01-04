package com.app.empire.scene.service.battle.buffer.specialbuf;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.role.objects.Living;

/** 物攻转换成魂攻 */
public class AttackConvertSoulAttackBuffer extends Buffer {
	private int changeValue = 0;

	public AttackConvertSoulAttackBuffer(Living source, Living target, SkillBuffer bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		int attack = source.getInitValue(EnumAttr.ATTACK);
		changeValue = attack * bufferInfo.getParam1() / 10000;
		source.refreshProperties(EnumAttr.ATTACK.getValue());
		source.refreshProperties(EnumAttr.SOUL_ATTACK.getValue());
	}

	public void dispose() {
		super.dispose();
		changeValue = 0;
		source.refreshProperties(EnumAttr.ATTACK.getValue());
		source.refreshProperties(EnumAttr.SOUL_ATTACK.getValue());

		System.out.println("--------------buffer 失效-------------------");
	}

	public int getResult(int type) {
		int result = 0;
		if (type == EnumAttr.ATTACK.getValue()) {
			result = -changeValue;
		}

		if (type == EnumAttr.SOUL_ATTACK.getValue()) {
			result = changeValue;
		}
		result = calSoullv(result, 2);
		return result;
	}

}
