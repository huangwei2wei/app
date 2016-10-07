package com.app.empire.scene.service.battle.buffer.specialbuf;

import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.battle.buffer.Buffer;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.entity.soul.SoulFuseSkillConfig;
import com.chuangyou.xianni.role.objects.Living;

/** 魂攻转换为物攻 */
public class SoulAttackConvertAttackBuffer extends Buffer {
	private int changeValue = 0;

	public SoulAttackConvertAttackBuffer(Living source, Living target, SkillBufferTemplateInfo bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		int soulAttack = source.getInitValue(EnumAttr.SOUL_ATTACK);
		changeValue = soulAttack * bufferInfo.getParam1() / 10000;

		source.refreshProperties(EnumAttr.SOUL_ATTACK.getValue());
		source.refreshProperties(EnumAttr.ATTACK.getValue());
	}

	public void dispose() {
		super.dispose();
		changeValue = 0;
		source.refreshProperties(EnumAttr.SOUL_ATTACK.getValue());
		source.refreshProperties(EnumAttr.ATTACK.getValue());
	}

	public int getResult(int type) {
		int result = 0;
		if (type == EnumAttr.SOUL_ATTACK.getValue()) {
			result = -changeValue;
		}
		if (type == EnumAttr.ATTACK.getValue()) {
			result = changeValue;
		}
		result = calSoullv(result, SoulFuseSkillConfig.EFFECT);
		return result;
	}
}
