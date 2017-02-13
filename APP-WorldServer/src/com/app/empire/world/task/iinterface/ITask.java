package com.app.empire.world.task.iinterface;

import com.chuangyou.xianni.entity.task.ITaskCfg;
import com.chuangyou.xianni.entity.task.ITaskInfo;

/**
 * 简单任务接口
 * @author laofan
 *
 */
public interface ITask extends ITaskTrigger{

	/**
	 * 获取任务静态模板表
	 * @return
	 */
	public ITaskCfg getTaskCfg();
	
	/**
	 * 获取任务动态数据
	 * @return
	 */
	public ITaskInfo getTaskInfo();
	
	/**
	 * 更新进度方法
	 * @param source：原来进度
	 * @param changeValue：要改变的进度值
	 */
	public void updateProcess(int source,int changeValue);
	
	/**
	 * 更新进度
	 * @param newValue
	 */
	public void updateProcess(int newValue);
	
	/**
	 * 初始化任务方法
	 */
	public void initTask();
	
	/**
	 * 任务是否完成
	 * @return
	 */
	public boolean isFinish();
	
}
