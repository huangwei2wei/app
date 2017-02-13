package com.app.empire.world.task.trigger;

import com.chuangyou.xianni.entity.task.TaskInfo;
import com.chuangyou.xianni.event.EventNameType;
import com.chuangyou.xianni.event.ObjectEvent;
import com.chuangyou.xianni.event.ObjectListener;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;

public class NpcDialogTaskTrigger extends KillMonsterTaskTrigger {

	public NpcDialogTaskTrigger(GamePlayer player, ITask task) {
		super(player, task);
		// TODO Auto-generated constructor stub
		this.eventType = EventNameType.TASK_NPC_DIALOG;
	}

	
	@Override
	public void addTrigger() {
		// TODO Auto-generated method stub
		removeTrigger();
		this.listener = new ObjectListener() {
			@Override
			public void onEvent(ObjectEvent event) {
				// TODO Auto-generated method stub
				if(getTask().getTaskInfo() instanceof TaskInfo){			
					if((int)event.getObject() == ((TaskInfo)getTask().getTaskInfo()).getTaskId()){
						getTask().updateProcess(getTask().getTaskInfo().getProcess(), 1);
					}
				}
				
			}
		};
		this.player.addListener(listener, eventType);
	}
	
	
	

	
	
	
}
