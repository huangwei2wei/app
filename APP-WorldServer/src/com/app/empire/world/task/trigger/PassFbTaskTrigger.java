package com.app.empire.world.task.trigger;

import com.chuangyou.xianni.event.EventNameType;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;

/**
 * 通关副本
 * @author laofan
 *
 */
public class PassFbTaskTrigger extends KillMonsterTaskTrigger {

	public PassFbTaskTrigger(GamePlayer player, ITask task) {
		super(player, task);
		this.eventType = EventNameType.TASK_PASS_FB;
	}

}
