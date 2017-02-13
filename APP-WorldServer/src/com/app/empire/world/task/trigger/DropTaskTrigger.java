package com.app.empire.world.task.trigger;

import com.chuangyou.xianni.drop.manager.DropManager;
import com.chuangyou.xianni.entity.task.TaskCfg;
import com.chuangyou.xianni.event.EventNameType;
import com.chuangyou.xianni.event.ObjectEvent;
import com.chuangyou.xianni.event.ObjectListener;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;


/**
 * 任务掉落观察者---(当有这些任务时。杀怪有掉落的奖励 )
 * @author laofan
 *
 */
public class DropTaskTrigger extends BaseTaskTrigger {

	public DropTaskTrigger(GamePlayer player, ITask task) {
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
				if(getTask().getTaskCfg() instanceof TaskCfg){					
					DropManager.dropTaskItems(player, ((TaskCfg)getTask().getTaskCfg()).getDropId());
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
