package com.app.empire.scene.service.battle.buffer.specialbuf;

import java.util.List;
import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.battle.AttackOrder;
import com.chuangyou.xianni.battle.buffer.Buffer;
import com.chuangyou.xianni.battle.buffer.BufferFactory;
import com.chuangyou.xianni.battle.buffer.BufferTargetType;
import com.chuangyou.xianni.battle.buffer.BufferType.FromType;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.battle.mgr.BattleTempMgr;
import com.chuangyou.xianni.battle.skill.FuseSkillVo;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.entity.soul.SoulFuseSkillConfig;
import com.chuangyou.xianni.role.helper.RoleConstants.RoleType;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Player;

/** 可以产生buffer的buffer */
public class BufferCreaterBuffer extends Buffer {

	public BufferCreaterBuffer(Living source, Living target, SkillBufferTemplateInfo bufferInfo) {
		super(source, target, bufferInfo);
	}

	@Override
	protected void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2) {
		SkillBufferTemplateInfo temp = getBufferInfo();
		int random = calSoulQuality(temp.getParam2());
		if (RND.next(10000) >= random) {
			return;
		}
		int bufferId = temp.getParam1();
		if (bufferId == 0) {
			return;
		}

		SkillBufferTemplateInfo sbinfo = BattleTempMgr.getBufferInfo(bufferId);
		if (sbinfo == null) {
			Log.error("cannot find buffer temp ,tempId : " + bufferId);
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

	private int calSoulQuality(int param) {
		if (bufferInfo.getFromType() == FromType.FUSE && source.getType() == RoleType.player) {
			Player player = (Player) source;
			FuseSkillVo fv = player.getFuseSkill(getTemplateId());
			if (fv != null) {
				SoulFuseSkillConfig config = BattleTempMgr.getFuseSkillTemp(getTemplateId());
				if (config != null) {
					int addValue = config.getChance(fv.getSkillColor()) * 100;
					param += addValue;
				} else {
					Log.error("SoulFuseSkillConfig " + getTemplateId());
				}
			}
		}
		return param;
	}

}
