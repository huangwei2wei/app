package com.app.empire.scene.service.battle.buffer.specialbuf;

import java.util.List;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.buffer.BufferFactory;
import com.app.empire.scene.service.battle.buffer.BufferTargetType;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.role.objects.Living;

/** 可以产生buffer的buffer */
public class BufferCreaterBuffer extends Buffer {

	public BufferCreaterBuffer(Living source, Living target, SkillBuffer bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		SkillBuffer temp = getBufferInfo();
		int random = 100;
		if (RND.next(10000) >= random) {
			return;
		}
		int bufferId = temp.getParam1();
		if (bufferId == 0) {
			return;
		}

		SkillBuffer sbinfo = ServiceManager.getManager().getGameConfigService().getSkillBufferTemps().get(bufferId);
		if (sbinfo == null) {
			log.error("cannot find buffer temp ,tempId : " + bufferId);
			return;
		}
		if (temp.getTargetType() == BufferTargetType.SOURCE) {
			Buffer buff = BufferFactory.createBuffer(source, source, sbinfo);
			source.addBuffer(buff);
		}

		if (temp.getTargetType() == BufferTargetType.SKILL_TARGET) {
			List<Living> targets = attackOrder.getTargets();
			if (targets == null) {
				return;
			}

			for (Living target : targets) {
				Buffer buff = BufferFactory.createBuffer(source, target, sbinfo);
				target.addBuffer(buff);
			}
		}
	}

}
