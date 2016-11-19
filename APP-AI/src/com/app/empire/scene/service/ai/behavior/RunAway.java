package com.app.empire.scene.service.ai.behavior;

import com.chuangyou.common.util.MathUtils;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.ai.AIState;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Monster;

public class RunAway extends MonsterBaseBehavior {

	public RunAway(Monster m) {
		super(AIState.RUNAWAY, m);
	}

	@Override
	public void exe() {
		Long attacker = getMonster().getAttacker();
		if (attacker == null)
			return;
		Living living = getMonster().getField().getLiving(attacker);
		if (living == null)
			return;
		Vector3 target = MathUtils.GetVector3InDistance2(living.getPostion(), getMonster().getPostion(), 2);
		if (!isValidPoint(target)) { // 该点不能达到
			target = MathUtils.GetVector3InDistance2(getMonster().getPostion(), living.getPostion(), 2);
			if (!isValidPoint(target))
				return;
		}
//		System.out.println("postion: " + living.getPostion() + " target: " + target);
		getMonster().stop(false);
		getMonster().moveto(target);
		getMonster().setAttacker(null);

	}

	@Override
	public AIState next() {
		if (!getMonster().isArrial())
			return AIState.INVALID;

		return AIState.IDLE;
	}

}
