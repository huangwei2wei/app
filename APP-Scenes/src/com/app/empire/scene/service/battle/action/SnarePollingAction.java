package com.app.empire.scene.service.battle.action;

import com.app.empire.scene.service.ai.proxy.SnareAI;
import com.app.empire.scene.service.ai.proxy.UpdatePosition;
import com.app.empire.scene.service.role.objects.Snare;

public class SnarePollingAction extends PollingAction {
	Snare					living;
	SnareAI					ai;
	UpdatePosition	updataPos;

	public SnarePollingAction(Snare snare, SnareAI ai, UpdatePosition updataPos) {
		super(snare, PollingAction.DELAY);
		this.living = snare;
		this.ai = ai;
		this.updataPos = updataPos;
	}

	@Override
	public void exec() {
		ai.exe();
		updataPos.exe();
	}

}
