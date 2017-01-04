package com.app.empire.scene.service.ai.behavior.snare;

import java.util.Set;

import com.app.empire.scene.service.ai.AIState;
import com.app.empire.scene.service.role.objects.Snare;
import com.app.empire.scene.service.warField.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.util.MathUtils;
import com.app.empire.scene.util.Vector3;

public class SnareMoveRandom extends SnareBaseBehavior {

	public SnareMoveRandom(Snare snare) {
		super(AIState.PATROL, snare);
	}

	@Override
	public void exe() {

		// 判断是否能巡逻
		if (!MathUtils.GetProbability(20)) {
			return;
		}
		Set<Integer> nearPlayer = getSnare().getNears(new PlayerSelectorHelper(getSnare()));
		if (nearPlayer.size() == 0) {
			return;
		}
		Vector3 patrolTarget = getSnare().getPostion();
		patrolTarget = MathUtils.GetRandomVector3ByCenter(getSnare().getPostion(), 10);
		if (!isValidPoint(patrolTarget)) { // 该点不能达到
			return;
		}

		getSnare().stop(false);
		getSnare().moveto(patrolTarget);
	}

	@Override
	public AIState next() {
		return null;
	}

}
