package com.app.empire.world.task.factory;

import com.chuangyou.xianni.entity.task.ITaskCfg;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskInitBehavior;
import com.chuangyou.xianni.retask.iinterface.ITriggerObserver;

/**
 * 抽象任务触发器工厂
 * @author laofan
 *
 */
public abstract class AbstractTaskTriggerFactory {
	
	/**
	 *  生产进度观察者
	 * @param cfg
	 * @return
	 */
	public abstract ITriggerObserver createObserver(ITaskCfg cfg,GamePlayer player,ITask task);
	
	
	/**
	 *  生产初始化任务处理器
	 * @param cfg
	 * @return
	 */
	public abstract ITaskInitBehavior createInitBehavior(ITaskCfg cfg,GamePlayer player,ITask task);
}
