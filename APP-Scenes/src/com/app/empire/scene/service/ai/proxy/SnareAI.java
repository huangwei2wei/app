package com.app.empire.scene.service.ai.proxy;

import com.app.empire.scene.constant.RoleConstants.RoleType;
import com.app.empire.scene.service.ai.behavior.snare.SnareBaseBehavior;
import com.app.empire.scene.service.ai.behavior.snare.SnareChase;
import com.app.empire.scene.service.ai.behavior.snare.SnareIdle;
import com.app.empire.scene.service.ai.behavior.snare.SnareMoveLine;
import com.app.empire.scene.service.ai.behavior.snare.SnareMoveRandom;
import com.app.empire.scene.service.battle.snare.SnareConstant.MoveType;
import com.app.empire.scene.service.role.objects.Snare;

public class SnareAI implements AI {
	private Snare				snare;
	private SnareBaseBehavior	behavior;

	public SnareAI(Snare snare) {
		this.snare = snare;
		if (snare.getCreater().getType() != RoleType.player) {
			behavior = createBehavior();
		}
	}

	@Override
	public void exe() {
		snare.exe();
		move();
	}

	public void move() {
		if (behavior == null || snare.isDie()) {
			return;
		}
		behavior.exe();
	}

	// 创建陷阱移动行为
	public SnareBaseBehavior createBehavior() {
		switch (snare.getSnareInfo().getMoveType()) {
			case MoveType.STANDING:
				return new SnareIdle(snare);
			case MoveType.MOVE_LINE:
				return new SnareMoveLine(snare);
			case MoveType.MOVE_LOCKING:
				return new SnareChase(snare);
			case MoveType.MOVE_RANDOM:
				return new SnareMoveRandom(snare);
			default:
				return new SnareIdle(snare);
		}
	}
}
