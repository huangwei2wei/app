package com.app.empire.scene.service.battle.buffer;

import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.battle.buffer.BufferType.FromType;
import com.chuangyou.xianni.battle.mgr.BattleTempMgr;
import com.chuangyou.xianni.battle.skill.FuseSkillVo;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.entity.soul.SoulFuseSkillConfig;
import com.chuangyou.xianni.role.helper.RoleConstants.RoleType;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Player;

/** 计算公式类buffer */
public abstract class FormulaBuffer extends Buffer {

	protected FormulaBuffer(Living source, Living target, SkillBufferTemplateInfo bufferInfo) {
		super(source, target, bufferInfo);
	}

	public abstract int calculation(int parameter, int parameter2);

	public int formulaExe(int parameter, int parameter2) {
		int result = 0;
		// 公式类型的buff不会调用exe方法，所以直接在此处扣费
		if (checkValid() && exeCost()) {
			result = calculation(parameter, parameter2);
		}
		// 如果buff来自魂幡，享受魂幡加成
		result = calSoullv(result, SoulFuseSkillConfig.EFFECT);
		return result;
	}
}
