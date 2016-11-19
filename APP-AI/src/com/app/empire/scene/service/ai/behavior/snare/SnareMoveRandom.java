package com.app.empire.scene.service.ai.behavior.snare;

import java.util.Set;

import com.chuangyou.common.util.MathUtils;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.ai.AIState;
import com.chuangyou.xianni.role.objects.Snare;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;

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
		Set<Long> nearPlayer = getSnare().getNears(new PlayerSelectorHelper(getSnare()));
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
