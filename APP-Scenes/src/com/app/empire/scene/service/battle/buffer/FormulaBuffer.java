package com.app.empire.scene.service.battle.buffer;

import com.app.db.mysql.entity.SkillBuffer;
import com.app.empire.scene.service.role.objects.Living;

/** 计算公式类buffer */
public abstract class FormulaBuffer extends Buffer {

	protected FormulaBuffer(Living source, Living target, SkillBuffer bufferInfo) {
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
		result = calSoullv(result, 2);
		return result;
	}
}
