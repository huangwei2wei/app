
package com.app.empire.scene.service.ai.proxy;

import java.util.HashMap;
import java.util.Map;

import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.ai.AIState;
import com.chuangyou.xianni.ai.behavior.Attack;
import com.chuangyou.xianni.ai.behavior.MonsterBaseBehavior;
import com.chuangyou.xianni.ai.behavior.BeAttack;
import com.chuangyou.xianni.ai.behavior.Chase;
import com.chuangyou.xianni.ai.behavior.Idle;
import com.chuangyou.xianni.ai.behavior.Patrol;
import com.chuangyou.xianni.ai.behavior.RunAway;
import com.chuangyou.xianni.ai.behavior.RunBack;
import com.chuangyou.xianni.config.SceneGlobal;
import com.chuangyou.xianni.cooldown.CoolDownTypes;
import com.chuangyou.xianni.entity.buffer.LivingState;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Monster;

public class MonsterAI implements AI {// extends BaseProxy {
	protected Map<AIState, MonsterBaseBehavior> behaviors;
	protected AIState current = AIState.IDLE;
	protected Living living;
	protected int delay;

	/**
	 * 仇恨计算频率
	 */

	public MonsterAI(Monster m) {
		this.living = m;
		behaviors = new HashMap<AIState, MonsterBaseBehavior>();
		createStates();
		// super(m, SceneGlobal.AI_MONSTER_DELAY);
		// recountHatred.setRate(SceneGlobal.AI_MONSTER_HETRED_RECOUNT);

	}

	// @Override
	public void exe() {
		// TODO Auto-generated method stub
		// AI已经死亡
		if (living.isDie())
			return;
		// 重新计算仇恨列表
		if (!living.isCooldowning(CoolDownTypes.RECOUNTHATRED, null)) {
			((Monster) living).countHatreds();
			living.addCooldown(CoolDownTypes.RECOUNTHATRED, null, SceneGlobal.AI_MONSTER_HETRED_RECOUNT);
			return;
		}
		if (living.isCooldowning(CoolDownTypes.BE_ATTACK, null)) {
			return;
		}
		// if (SceneManagers.cooldownManager.isCooldowning(living,
		// CoolDownTypes.FIXED_BODY, null)) {
		// ((Monster) living).stop(false);
		// return;
		// }
		if (!living.checkStatus(EnumBufferState.MOVE)) {
			((Monster) living).stop(false);
			return;
		}

		AIState next = behaviors.get(current).next();

//		if (living.getId() == 1000000000083L) {
//			System.out.println("怪物 id： " + living.getId() + " 状态： " + current + " 下一个状态：" + next + " 位置：" + living.getPostion() + " 目标：" + living.getTargetPostion() + "距离："
//					+ Vector3.distance(living.getPostion(), living.getTargetPostion()));
//		}

		if (next == AIState.INVALID)
			return;
		current = next;
		// System.out.println(living.getId() + " 状态："+current);
		if (behaviors.containsKey(current))
			behaviors.get(current).exe();
	}

	// @Override
	protected void createStates() {
		// TODO Auto-generated method stub
		behaviors.put(AIState.IDLE, new Idle((Monster) living));
		behaviors.put(AIState.PATROL, new Patrol((Monster) living));
		behaviors.put(AIState.BEATTACK, new BeAttack((Monster) living));
		behaviors.put(AIState.ATTACK, new Attack((Monster) living));
		behaviors.put(AIState.CHASE, new Chase((Monster) living));
		behaviors.put(AIState.RUNBACK, new RunBack((Monster) living));
		behaviors.put(AIState.RUNAWAY, new RunAway((Monster) living));
	}

}
