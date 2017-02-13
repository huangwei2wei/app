package com.app.empire.world.task.behavior.process;

import com.chuangyou.xianni.entity.state.StateConditionInfo;
import com.chuangyou.xianni.event.EventNameType;
import com.chuangyou.xianni.event.ObjectEvent;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskProcessBehavior;

/**
 * 境界任务进度形为处理器
 * @author laofan
 *
 */
public class StateTaskProcessBehavior implements ITaskProcessBehavior {

	/**
	 * 任务实体
	 */
	private final ITask task;
	
	private final GamePlayer player;
	
	public StateTaskProcessBehavior(ITask task,GamePlayer player) {
		super();
		this.task = task;
		this.player = player;
	}

	@Override
	public ITask getTask() {
		// TODO Auto-generated method stub
		return task;
	}

	/**
	 * 1：进度只能增加，不减
	 * 2：当进度完成后。就取消观察
	 */
	@Override
	public void process(int num) {
		// TODO Auto-generated method stub
		if(!task.isFinish()){	
			if(task.getTaskInfo().getProcess()<num){  //只有增加才会更新（境界任务特有的）		
				task.getTaskInfo().setProcess(num);	
				if(task.getTaskInfo() instanceof StateConditionInfo){
					player.sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_STATE_UPDATE,((StateConditionInfo)task.getTaskInfo()).getMsg()));
				}
				if(task.isFinish()){
					this.player.notifyListeners(new ObjectEvent(this, null, EventNameType.STATE_TAKS_FINISH));
					task.removeTrigger();
				}
			}
		}else{  //取消观察者
			task.removeTrigger();
		}
	}

}
