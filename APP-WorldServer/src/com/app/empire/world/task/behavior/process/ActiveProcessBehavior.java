package com.app.empire.world.task.behavior.process;

import java.util.Date;

import com.chuangyou.xianni.entity.active.ActiveConfig;
import com.chuangyou.xianni.entity.active.ActiveInfo;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskProcessBehavior;
import com.chuangyou.xianni.retask.vo.ActiveTask;

public class ActiveProcessBehavior implements ITaskProcessBehavior {

	/**
	 * 任务实体
	 */
	protected final ITask task;
	
	protected final GamePlayer player;
	
	public ActiveProcessBehavior(ITask task,GamePlayer player) {
		super();
		this.task = task;
		this.player = player;
	}

	
	@Override
	public ITask getTask() {
		// TODO Auto-generated method stub
		return task;
	}

	
	private ActiveConfig getConfig(){
		return (ActiveConfig) task.getTaskCfg();
	}
	
	@Override
	public void process(int num) {
		// TODO Auto-generated method stub
		if(getConfig().getNeedLv()>player.getBasePlayer().getPlayerInfo().getLevel()){
			return;
		}
		if(!task.isFinish()){	
			if(task.getTaskInfo().getProcess()<num){  //只有增加才会更新（境界任务特有的）		
				this.doUpdate(num);
				if(task.isFinish()){
					task.removeTrigger();
					((ActiveTask)getTask()).doTaskFinish();
				}
			}
		}else{  //取消观察者
			task.removeTrigger();
		}
	}

	
	/**
	 * 执行更新
	 * @param num
	 */
	protected void doUpdate(int num){
		task.getTaskInfo().setProcess(num);	
		((ActiveInfo)task.getTaskInfo()).setUpdateTime(new Date());
		if(task.getTaskInfo() instanceof ActiveInfo){
			player.sendPbMessage(MessageUtil.buildMessage(Protocol.U_NOTIFY_ACTIVE_PROCESS,((ActiveInfo)task.getTaskInfo()).getMsg()));
		}
	}
}
