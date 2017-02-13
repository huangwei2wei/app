package com.app.empire.world.task.behavior.process;

import java.util.Date;

import com.chuangyou.xianni.entity.Option;
import com.chuangyou.xianni.entity.task.TaskCfg;
import com.chuangyou.xianni.entity.task.TaskInfo;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskProcessBehavior;
import com.chuangyou.xianni.retask.vo.RealTask;

/**
 * 真正任务进度处理器
 * @author laofan
 *
 */
public class RealTaskPRocessBehavior implements ITaskProcessBehavior {

	/**
	 * 任务实体
	 */
	protected final ITask task;
	
	protected final GamePlayer player;
	
	public RealTaskPRocessBehavior(ITask task,GamePlayer player) {
		this.task = task;
		this.player = player;
	}

	@Override
	public ITask getTask() {
		// TODO Auto-generated method stub
		return task;
	}
	
	public TaskInfo getInfo(){
		return (TaskInfo) task.getTaskInfo();
	}
	
	public TaskCfg getConfig(){
		return (TaskCfg) task.getTaskCfg();
	}
	
	@Override
	public void process(int num) {
		// TODO Auto-generated method stub
		if(((RealTask)getTask()).isTimeout()){
			return;
		}
		
		if(getInfo().getState()==TaskInfo.ACCEPT || getInfo().getState()==TaskInfo.FINISH){
		
				getInfo().setUpdateTime(new Date());
				getInfo().setProcess(num);
				getInfo().setOp(Option.Update);
				((RealTask)getTask()).notifyMsg();			
				if(getTask().isFinish()){
					((RealTask)getTask()).doFinish();
				}
			
		}
	}

}
