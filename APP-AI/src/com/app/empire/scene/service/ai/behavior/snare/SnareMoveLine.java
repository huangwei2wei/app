package com.app.empire.scene.service.ai.behavior.snare;

import com.chuangyou.common.util.MathUtils;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.ai.AIState;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.role.objects.Snare;

public class SnareMoveLine extends SnareBaseBehavior {
	private boolean isMoved = false;

	public SnareMoveLine(Snare snare) {
		super(AIState.PATROL, snare);
	}

	@Override
	public void exe() {
		// 直线移动的陷阱，只做一次移动
		if (isMoved) {
			return;
		}
		isMoved = true;
		Living target = getSnare().getTarget();
		if (target == null) {
			return;
		}
		getSnare().getCreater().setDir(MathUtils.getDirByXZ(target.getPostion(), getSnare().getCreater().getPostion()));
		Vector3 tp = Vector3.add(getSnare().getCreater().getPostion(), Vector3.multipy(getSnare().getCreater().getDir(), getSnare().getSnareInfo().getDistance()));

		getSnare().moveto(tp);
	}

	@Override
	public AIState next() {
		return null;
	}

}
