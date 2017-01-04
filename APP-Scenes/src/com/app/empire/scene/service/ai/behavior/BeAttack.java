package com.app.empire.scene.service.ai.behavior;

import com.app.empire.scene.service.ai.AIState;
import com.app.empire.scene.service.cooldown.CoolDownTypes;
import com.app.empire.scene.service.role.objects.Monster;

/**
 * 受击状态
 * 
 * @author
 * 
 */
public class BeAttack extends MonsterBaseBehavior {

	public BeAttack(Monster m) {
		super(AIState.BEATTACK, m);
	}

	@Override
	public AIState next() {
		// 受击cd中，为了同步客户端做受击动画处理
		if (getMonster().isCooldowning(CoolDownTypes.BE_ATTACK, null))
			return AIState.INVALID;
		return AIState.IDLE;
	}

	@Override
	public void exe() {
	}

}
