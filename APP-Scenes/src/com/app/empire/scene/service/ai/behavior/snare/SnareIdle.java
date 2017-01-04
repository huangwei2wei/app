package com.app.empire.scene.service.ai.behavior.snare;

import com.app.empire.scene.service.ai.AIState;
import com.app.empire.scene.service.role.objects.Snare;

public class SnareIdle extends SnareBaseBehavior {

	public SnareIdle(Snare snare) {
		super(AIState.IDLE, snare);
	}

	@Override
	public void exe() {

	}

	@Override
	public AIState next() {
		return null;
	}

}
