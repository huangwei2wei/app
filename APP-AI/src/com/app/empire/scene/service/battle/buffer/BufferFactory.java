package com.app.empire.scene.service.battle.buffer;

import com.chuangyou.xianni.battle.buffer.specialbuf.AttackConvertSoulAttackBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.AttributesBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.BeAttackDamageEffectBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.BombBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.BufferCreaterBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.CasterDamageEffectBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.CritEffectBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.DefenceBreakBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.SoulAttackConvertAttackBuffer;
import com.chuangyou.xianni.battle.buffer.specialbuf.SoulDefenceBreakBuffer;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.role.objects.Living;

/**
 * BufferFactory创建的工厂类
 */
public class BufferFactory {

	/**
	 * 建立BUFFER
	 */
	public static Buffer createBuffer(Living source, Living target, SkillBufferTemplateInfo bufferInfo) {
		Buffer buffer = null;
		switch (bufferInfo.getType()) {
			case BufferType.COMMON_DAMANGE:
			case BufferType.ONLY_BLOOD:
			case BufferType.ONLY_SOUL:
			case BufferType.COMMON_RESTORE:
			case BufferType.ONLY_RESTORE_BLOOD:
			case BufferType.ONLY_RESTORE_SOUL:
				buffer = new LivingDamageBuffer(source, target, bufferInfo);
				break;
			case BufferType.ATTR_BODY:
				buffer = new AttributesBuffer(source, target, bufferInfo);
				break;
			case BufferType.CRIT_4_BLOOD:
				buffer = new CritEffectBuffer(source, target, bufferInfo);
				break;
			case BufferType.BUFFER_CREATER:
				buffer = new BufferCreaterBuffer(source, target, bufferInfo);
				break;
			case BufferType.ATTACK_COVENT_SOULATTACK:
				buffer = new AttackConvertSoulAttackBuffer(source, target, bufferInfo);
				break;
			case BufferType.SOULATTACK_COVENT_ATTACK:
				buffer = new SoulAttackConvertAttackBuffer(source, target, bufferInfo);
				break;
			case BufferType.BOMB_DAMAGE:
				buffer = new BombBuffer(source, target, bufferInfo);
				break;
			case BufferType.DEFENCE_BREAK:
				buffer = new DefenceBreakBuffer(source, target, bufferInfo);
				break;
			case BufferType.SOUL_DEFECT_BREAK:
				buffer = new SoulDefenceBreakBuffer(source, target, bufferInfo);
				break;
			case BufferType.CASTER_DAMAGE_EFFECT:
				buffer = new CasterDamageEffectBuffer(source, target, bufferInfo);
				break;

			case BufferType.BE_ATTACK_DAMAGE_EFFECT:
				buffer = new BeAttackDamageEffectBuffer(source, target, bufferInfo);
				break;
			default:
				buffer = new LivingDamageBuffer(source, target, bufferInfo);
		}
		buffer.setExecuted(false);
		return buffer;
	}
}
