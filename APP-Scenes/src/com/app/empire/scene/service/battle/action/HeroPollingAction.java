package com.app.empire.scene.service.battle.action;

import com.app.empire.scene.service.role.objects.Player;


/**
 * 英雄自检动作/buffer运算
 */
public class HeroPollingAction extends PollingAction {

	public HeroPollingAction(Player player) {
		super(player, PollingAction.DELAY);
	}

	@Override
	public void exec() {

	}

}
