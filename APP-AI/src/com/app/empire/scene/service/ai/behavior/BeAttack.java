package com.app.empire.scene.service.ai.behavior;

import com.chuangyou.xianni.ai.AIState;
import com.chuangyou.xianni.cooldown.CoolDownTypes;
import com.chuangyou.xianni.role.objects.Monster;

/**
 * 受击状态
 * @author wkghost
 *
 */
public class BeAttack extends MonsterBaseBehavior {

	public BeAttack(Monster m) {
		super(AIState.BEATTACK, m);
	}

	@Override
	public AIState next() {
		//受击cd中，为了同步客户端做受击动画处理
		if(getMonster().isCooldowning(CoolDownTypes.BE_ATTACK, null))
			return AIState.INVALID;
		return AIState.IDLE;
	}

	@Override
	public void exe() {
		// TODO Auto-generated method stub
	}

}
