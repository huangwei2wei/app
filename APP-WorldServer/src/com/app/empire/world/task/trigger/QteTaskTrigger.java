package com.app.empire.world.task.trigger;

import com.chuangyou.xianni.event.EventNameType;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;

/**
 *  QTE任务
 * @author laofan
 *
 */
public class QteTaskTrigger extends NpcDialogTaskTrigger{

	public QteTaskTrigger(GamePlayer player, ITask task) {
		super(player, task);
		// TODO Auto-generated constructor stub
		this.eventType = EventNameType.TASK_QTE;
	}

}
