package com.app.empire.scene.util.exec;

import java.util.Random;

/**
 * 线程管理类
 */
public class ThreadManager {

	static Random r = new Random();
	// 用户响应动作执行线程池
	public static ActionExecutor actionExecutor;

	static {
		int corePoolSize = 8;
		int maxPoolSize = 32;
		int keepAliveTime = 5;
		int cacheSize = 64;
		actionExecutor = new ActionExecutor(corePoolSize, maxPoolSize, keepAliveTime, cacheSize, "WORD_ACTION_EXECUTOR");
	}

	// 执行战斗线程池
	public static ActionExecutor battleExecutor;
	static {
		int corePoolSize = 8;
		int maxPoolSize = 32;
		int keepAliveTime = 5;
		int cacheSize = 64;
		battleExecutor = new ActionExecutor(corePoolSize, maxPoolSize, keepAliveTime, cacheSize, "WORD_BATTLE_EXECUTOR");
	}
}
