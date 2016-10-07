package com.app.empire.scene.service.battle.action;

import com.app.empire.scene.service.role.action.UpdatePositionAction;
import com.chuangyou.xianni.ai.proxy.MonsterAI;
import com.chuangyou.xianni.role.objects.Monster;

public class MonsterPollingAction extends PollingAction {
	private MonsterAI				ai;
	private UpdatePositionAction	upPos;

	public MonsterPollingAction(Monster monster, MonsterAI ai, UpdatePositionAction upPos) {
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
