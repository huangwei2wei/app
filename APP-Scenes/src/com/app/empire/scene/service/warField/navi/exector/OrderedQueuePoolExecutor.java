package com.app.empire.scene.service.warField.navi.exector;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OrderedQueuePoolExecutor extends ThreadPoolExecutor {
	private OrderedQueuePool<Integer, NavigationTask> pool = new OrderedQueuePool<Integer, NavigationTask>();

	private String name;
	private int maxQueueSize;

	public OrderedQueuePoolExecutor(String name, int corePoolSize, int maxQueueSize) {
		super(corePoolSize + 1, 2 * corePoolSize, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		this.name = name;
		this.maxQueueSize = maxQueueSize;
	}

	public OrderedQueuePoolExecutor(int corePoolSize, int maxQuequSize) {
		this("queue-pool", corePoolSize, maxQuequSize);
	}

	public void removeTaskQueue(Integer id) {
		pool.removeTasksQueue(id);
	}

	/**
	 * 增加执行任务
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean addTask(NavigationTask task) {
		TasksQueue<NavigationTask> queue = pool.getTasksQueue(task.getFieldId());
		boolean run = false;
		boolean result = false;
		synchronized (queue) {
			if (maxQueueSize > 0) {
				if (queue.size() > maxQueueSize) {
					queue.clear();
				}
			}
			result = queue.add(task);
			if (result) {
				task.setTasksQueue(queue);
				{
					if (queue.isProcessingCompleted()) {
						queue.setProcessingCompleted(false);
						run = true;
					}
				}
			} else {
				// log.error("队列添加任务失败");
				System.err.println("队列添加任务失败");
			}
		}
		if (run) {
			execute(queue.poll());
		}
		return result;
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);

		NavigationTask work = (NavigationTask) r;
		TasksQueue<NavigationTask> queue = work.getTasksQueue();
		if (queue != null) {
			NavigationTask afterWork = null;
			synchronized (queue) {
				afterWork = queue.poll();
				if (afterWork == null) {
					queue.setProcessingCompleted(true);
				}
			}
			if (afterWork != null) {
				execute(afterWork);
			}
		} else {
			// log.error("执行队列为空");
			System.err.println("执行队列为空");
		}
	}
}
