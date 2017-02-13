package com.app.empire.world.task.trigger;

import com.chuangyou.xianni.event.ObjectListener;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITriggerObserver;

public abstract class BaseTaskTrigger implements ITriggerObserver {

	
	protected final GamePlayer player;
	
	private final ITask task;
	
	protected ObjectListener listener;
	
	protected int eventType;
	
	
	public BaseTaskTrigger(GamePlayer player, ITask task) {
		super();
		this.player = player;
		this.task = task;
	}

	@Override
	public ITask getTask() {
		// TODO Auto-generated method stub
		return task;
	}

}
