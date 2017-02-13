package com.app.empire.world.task.behavior.process;

import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;

/**
 * 总计算活跃系统进度行为处理
 * @author laofan
 *
 */
public class TotalActiveProcessBehavior extends ActiveProcessBehavior{

	public TotalActiveProcessBehavior(ITask task, GamePlayer player) {
		super(task, player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(int num) {
		// TODO Auto-generated method stub
		if(task.getTaskInfo().getProcess()<num){  //只有增加才会更新（境界任务特有的）		
			this.doUpdate(num);
		}
	}
	
	
	

}
