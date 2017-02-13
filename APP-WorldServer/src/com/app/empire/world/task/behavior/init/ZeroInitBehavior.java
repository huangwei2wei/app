package com.app.empire.world.task.behavior.init;

import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskInitBehavior;

/**
 * 0进度初始化形为处理器
 * @author laofan
 *
 */
public class ZeroInitBehavior implements ITaskInitBehavior {

	@SuppressWarnings("unused")
	private final GamePlayer player;
	private final ITask task;
	
	
	
	public ZeroInitBehavior(GamePlayer player, ITask task) {
		super();
		this.player = player;
		this.task = task;
	}

	@Override
	public ITask getTask() {
		// TODO Auto-generated method stub
		return task;
	}

	@Override
	public void initTask() {
		// TODO Auto-generated method stub
		task.getTaskInfo().setProcess(0);
	}

}
