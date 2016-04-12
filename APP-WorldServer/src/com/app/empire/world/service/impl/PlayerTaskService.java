package com.app.empire.world.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.dao.mongo.impl.PlayerTaskDao;
import com.app.empire.world.entity.mongo.PlayerTask;
import com.app.empire.world.entity.mongo.TaskEntry;
import com.app.empire.world.entity.mysql.gameConfig.Task;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.thread.ThreadPool;

/**
 * 任务服务
 * 
 * @author doter
 */
@Service
@SuppressWarnings("rawtypes")
public class PlayerTaskService {
	private Logger log = Logger.getLogger(PlayerTaskService.class);
	private HandlerThreadPool threadPool = new HandlerThreadPool(2, 4, 100);
	@Autowired
	private PlayerTaskDao playerTaskDao;
	private static Map<Integer, Map> baseTask = new HashMap<Integer, Map>();

	public void init() {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		baseTask = gameConfigService.getGameConfig().get(Task.class.getSimpleName());
	}
	/** 根据玩家获取任务 */
	public PlayerTask getPlayerListByAccountId(WorldPlayer worldPlayer) {
		PlayerTask playerTask = playerTaskDao.getPlayerListByPlayerId(worldPlayer.getPlayer().getId());
		if (playerTask == null)
			playerTask = this.initPlayerTask(worldPlayer);
		worldPlayer.setPlayerTask(playerTask);
		return worldPlayer.getPlayerTask();
	}
	/** 初始化玩家任务 领取前置id为0的任务 **/
	public PlayerTask initPlayerTask(WorldPlayer worldPlayer) {
		PlayerTask newPlayerTask = new PlayerTask();
		Map<Integer, TaskEntry> tasks = new HashMap<Integer, TaskEntry>();
		Map<Integer, TaskEntry> playerTask = worldPlayer.getPlayerTask().getTasks();
		for (Entry<Integer, Map> entry : PlayerTaskService.baseTask.entrySet()) {
			Integer taskId = entry.getKey();
			if (playerTask.containsKey(taskId))
				continue;
			Integer preTaskId = Integer.parseInt(entry.getValue().get("preTaskId").toString());
			if (preTaskId == 0) {// 第一个引导任务
				Integer id = Integer.parseInt(entry.getValue().get("id").toString());
				Short type = Short.parseShort(entry.getValue().get("type").toString());
				Short subType = Short.parseShort(entry.getValue().get("subType").toString());
				Integer nextTaskId = Integer.parseInt(entry.getValue().get("nextTaskId").toString());
				String activationCondition = entry.getValue().get("activationCondition").toString();
				String completeCondition = entry.getValue().get("completeCondition").toString();
				String completeAward = entry.getValue().get("completeAward").toString();
				TaskEntry newTask = new TaskEntry();
				newTask.setType(type);
				newTask.setSubType(subType);
				newTask.setNextTaskId(nextTaskId);
				newTask.setActivationCondition(activationCondition);
				newTask.setCompleteCondition(completeCondition);
				newTask.setCompleteAward(completeAward);
				tasks.put(id, newTask);
			}
		}
		newPlayerTask.setPlayerId(worldPlayer.getPlayer().getId());
		newPlayerTask.setTasks(tasks);
		return newPlayerTask;
	}
	/**
	 * 根据玩家和任务Id 获取任务
	 */
	public TaskEntry loadPlayerTask(WorldPlayer worldPlayer, int taskId) {
		PlayerTask playerTask = this.getPlayerListByAccountId(worldPlayer);
		return playerTask.getTasks().get(taskId);
	}

	/**
	 * 自动检测完成任务
	 */
	public void autoCompleteTask(WorldPlayer worldPlayer) {
		threadPool.execute(new autoCompleteTask(worldPlayer));
	}
	/** 自动检测完成任务 接取下一个任务 **/
	static class autoCompleteTask implements Runnable {
		private WorldPlayer worldPlayer;
		// 构造函数
		public autoCompleteTask(WorldPlayer worldPlayer) {
			this.worldPlayer = worldPlayer;
		}
		@Override
		public void run() {
			try {
				PlayerTask playerTask = worldPlayer.getPlayerTask();
				Map<Integer, TaskEntry> tasks = playerTask.getTasks();
				for (Entry<Integer, TaskEntry> entry : tasks.entrySet()) {
					Integer taskId = entry.getKey();
					TaskEntry task = entry.getValue();

				}
			} catch (Throwable e) {
				Log.info(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	class HandlerThreadPool extends ThreadPool {
		/**
		 * 构造函数，初始化线程池及队列
		 * 
		 * @param minPoolSize 最小线程数
		 * @param maxPoolSize 最大线程数
		 * @param queurSize 等待队列大小
		 */
		public HandlerThreadPool(int minPoolSize, int maxPoolSize, int queurSize) {
			super(minPoolSize, maxPoolSize, queurSize);
		}
		/**
		 * 线程池已满,拒绝线程
		 * 
		 * @param runnable 任务信息
		 * @param threadPoolExecutor 异常信息
		 */
		@Override
		public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
			log.info("玩家任务线程池 Is Full...");
		}
	}
}
