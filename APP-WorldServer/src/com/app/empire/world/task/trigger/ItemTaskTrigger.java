package com.app.empire.world.task.trigger;

import com.chuangyou.xianni.event.EventNameType;
import com.chuangyou.xianni.event.ObjectEvent;
import com.chuangyou.xianni.event.ObjectListener;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskInitBehavior;

/**
 * 物品相关任务条件
 * @author laofan
 *
 */
public class ItemTaskTrigger extends BaseTaskTrigger implements ITaskInitBehavior{

	private ObjectListener reduceListener;
	
	public ItemTaskTrigger(GamePlayer player, ITask task) {
		super(player, task);
		// TODO Auto-generated constructor stub
		this.eventType = EventNameType.TASK_ITEM_CHANGE_ADD;
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
					doUpdate();
				}
			}
		};
		this.player.getBagInventory().addListener(listener, eventType);
		reduceListener =  new ObjectListener() {
			
			@Override
			public void onEvent(ObjectEvent event) {
				// TODO Auto-generated method stub
				if((int)event.getObject() == getTask().getTaskCfg().getTargetId()){
					doUpdate();
				}
			}
		};
		this.player.getBagInventory().addListener(reduceListener,EventNameType.TASK_ITEM_CHANGE_REDUCE);
	}

	
	private void doUpdate(){
		int num = getTask().getTaskInfo().getProcess();
		int nowNum = this.player.getBagInventory().getItemCount(getTask().getTaskCfg().getTargetId());
		if(num!=nowNum){
			int temp = nowNum - num;
			getTask().updateProcess(num, temp);
		}
	}
	
	@Override
	public void removeTrigger() {
		// TODO Auto-generated method stub
		this.player.getBagInventory().removeListener(listener, eventType);
		this.player.getBagInventory().removeListener(reduceListener,EventNameType.TASK_ITEM_CHANGE_REDUCE);
	}

	@Override
	public void initTask() {
		// TODO Auto-generated method stub
		int nowNum = this.player.getBagInventory().getItemCount(getTask().getTaskCfg().getTargetId());
		getTask().getTaskInfo().setProcess(nowNum);		
	}

	
	
	


	

}
