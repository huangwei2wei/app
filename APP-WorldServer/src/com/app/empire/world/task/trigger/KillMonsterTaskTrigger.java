package com.app.empire.world.task.trigger;

import com.chuangyou.xianni.event.EventNameType;
import com.chuangyou.xianni.event.ObjectEvent;
import com.chuangyou.xianni.event.ObjectListener;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;

/**
 * 杀怪计数监听器
 * @author laofan
 *
 */
public class KillMonsterTaskTrigger extends BaseTaskTrigger {

	
	public KillMonsterTaskTrigger(GamePlayer player, ITask task) {
		super(player, task);
		// TODO Auto-generated constructor stub
		eventType = EventNameType.TASK_KILL_MONSTER;
	}


	@Override
	public void addTrigger() {
		// TODO Auto-generated method stub
		removeTrigger();
		this.listener = new ObjectListener() {
			@Override
			public void onEvent(ObjectEvent event) {
				// TODO Auto-generated method stub
				if((int)event.getObject() == getTask().getTaskCfg().getTargetId()){
					getTask().updateProcess(getTask().getTaskInfo().getProcess(), 1);
				}
			}
		};
		this.player.addListener(listener, eventType);
	}

	@Override
	public void removeTrigger() {
		// TODO Auto-generated method stub
		this.player.removeListener(listener, eventType);
	}


}
