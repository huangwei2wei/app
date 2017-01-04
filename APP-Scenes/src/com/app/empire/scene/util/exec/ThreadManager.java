package com.app.empire.scene.util.exec;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 线程管理类
 */
public class ThreadManager {

	static Random r = new Random();
	// 用户响应动作执行线程池
	public static ActionExecutor actionExecutor;
	// 用户请求动作执行线程池
	public static ActionExecutor cmdExecutor;
	// 执行战斗线程池
	public static ActionExecutor battleExecutor;

	public static final short WORLD_ACTION_QUEUE_NUM = 100;
	// 用户响应动作执行队列
	private static Map<Integer, AbstractActionQueue> ActionQueues = new HashMap<Integer, AbstractActionQueue>();

	public static AbstractActionQueue getActionRandom() {
		return ActionQueues.get(r.nextInt(WORLD_ACTION_QUEUE_NUM));
	}

	static {
		int corePoolSize = 8;
		int maxPoolSize = 32;
		int keepAliveTime = 5;
		int cacheSize = 64;
		actionExecutor = new ActionExecutor(corePoolSize, maxPoolSize, keepAliveTime, cacheSize, "WORD_ACTION_EXECUTOR");
		for (int i = 0; i <= WORLD_ACTION_QUEUE_NUM + 2; i++) {
			AbstractActionQueue actionQueue = new AbstractActionQueue(actionExecutor);
			ActionQueues.put(i, actionQueue);
		}
	}

	static {
		int corePoolSize = 8;
		int maxPoolSize = 32;
		int keepAliveTime = 5;
		int cacheSize = 64;
		cmdExecutor = new ActionExecutor(corePoolSize, maxPoolSize, keepAliveTime, cacheSize, "WORD_CMD_TASK_EXECUTOR");
	}

	static {
		int corePoolSize = 8;
		int maxPoolSize = 32;
		int keepAliveTime = 5;
		int cacheSize = 64;
		battleExecutor = new ActionExecutor(corePoolSize, maxPoolSize, keepAliveTime, cacheSize, "WORD_BATTLE_EXECUTOR");
	}
}
