package com.app.empire.world.entity.mongo;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.app.db.mongo.entity.IEntity;

/**
 * 玩家任务
 * 
 * @author doter
 */
@Document(collection = "Player_task")
public class PlayerTask extends IEntity {
	private int playerId;// 用户id
	private Map<Integer, TaskEntry> tasks;// 任务Id:任务

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public Map<Integer, TaskEntry> getTasks() {
		return tasks;
	}
	public void setTasks(Map<Integer, TaskEntry> tasks) {
		this.tasks = tasks;
	}

}
