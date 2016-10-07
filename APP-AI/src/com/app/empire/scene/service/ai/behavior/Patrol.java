package com.app.empire.scene.service.ai.behavior;

import java.util.Set;

import com.chuangyou.common.util.MathUtils;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.ai.AIState;
import com.chuangyou.xianni.cooldown.CoolDownTypes;
import com.chuangyou.xianni.role.objects.Monster;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;

public class Patrol extends MonsterBaseBehavior {

	private final int PatrolProbability = 30;
	private Vector3 patrolTarget;
	// 可以巡逻
	private boolean needPatrol = false;
	// private Selector playerSelector;// = new PlayerSelectorHelper();

	public Patrol(Monster m) {
		super(AIState.PATROL, m);
		// playerSelector = new PlayerSelectorHelper(getMonster());
	}

	@Override
	public void exe() {
		needPatrol = true;

		int patrolRange = getMonster().getMonsterInfo().getSeekEnemyRange();
		if (patrolRange == 0) {
			needPatrol = false;
			return;
		}
		if (patrolRange > 0) {
			// 判断是否能巡逻
			if (!MathUtils.GetProbability(PatrolProbability)) {
				needPatrol = false;
				return;
			}
		}
		Set<Long> nearPlayer = getMonster().getNears(new PlayerSelectorHelper(getMonster()));
		if(nearPlayer.size() == 0) return;
		patrolTarget = getMonster().getPostion();
		patrolTarget = MathUtils.GetRandomVector3ByCenter(getMonster().getInitPosition(), patrolRange);
		if (!isValidPoint(patrolTarget)) {	// 该点不能达到
			needPatrol = false;
			return;
		}

		getMonster().stop(false);
		getMonster().moveto(patrolTarget);

	}

	@Override
	public AIState next() {
		if (getMonster().getAiConfig()!=null && getMonster().getAiConfig().isRunAway()) {// 是否逃跑
			if (getMonster().getAttacker() != null)
				return AIState.RUNAWAY;
		}

		// if (!needPatrol)
		// return AIState.IDLE; // 距离太短，不处理
		if (getMonster().isCooldowning(CoolDownTypes.BE_ATTACK, null))
			return AIState.BEATTACK;
		if (!getMonster().isArrial()) {
			return AIState.INVALID;
		}
		// float distance = Vector3.distance(getMonster().getInitPosition(), getMonster().getPostion());// 出生点与目标的距离
		// if (distance > getMonster().getMonsterInfo().getSeekEnemyRange())
		// return AIState.RUNBACK;

		return AIState.IDLE;
	}

}
