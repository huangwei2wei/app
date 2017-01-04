package com.app.empire.scene.service.battle.action;

import com.app.empire.scene.service.ai.proxy.MonsterAI;
import com.app.empire.scene.service.ai.proxy.UpdatePosition;
import com.app.empire.scene.service.role.objects.Monster;

public class MonsterPollingAction extends PollingAction {
	private MonsterAI				ai;
	private UpdatePosition	upPos;

	public MonsterPollingAction(Monster monster, MonsterAI ai, UpdatePosition upPos) {
		super(monster, PollingAction.DELAY);
		this.ai = ai;
		this.upPos = upPos;
	}

	@Override
	public void exec() {
	
		ai.exe();

		upPos.exe();
	}

}
