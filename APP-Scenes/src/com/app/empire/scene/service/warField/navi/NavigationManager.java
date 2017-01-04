package com.app.empire.scene.service.warField.navi;

import com.app.empire.scene.service.warField.navi.exector.NavigationTask;
import com.app.empire.scene.service.warField.navi.exector.OrderedQueuePoolExecutor;

public class NavigationManager {

	private static NavigationManager instance = new NavigationManager();

	private NavigationManager() {

	}

	public static NavigationManager getInstance() {
		return instance;
	}

	private OrderedQueuePoolExecutor executor = new OrderedQueuePoolExecutor("寻路组件", Runtime.getRuntime().availableProcessors(), 10000);

	/**
	 * 添加任务到队列
	 * 
	 * @param key
	 * @param task
	 * @return
	 */
	public void addTask(NavigationTask task) {
		executor.addTask(task);
	}

	/**
	 * 移除任务队列
	 * 
	 * @param key
	 */
	public void removeTaskQueue(int fid) {
		executor.removeTaskQueue(fid);
	}
}
