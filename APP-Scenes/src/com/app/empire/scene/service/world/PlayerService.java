package com.app.empire.scene.service.world;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * 世界管理类，管理用户以及用户连接
 */

@Service
public class PlayerService {
	public ConcurrentHashMap<Integer, ArmyProxy> players = new ConcurrentHashMap<Integer, ArmyProxy>();

	public void addOnline(ArmyProxy army) {
		players.put(army.getPlayerId(), army);
		// army.setOnlineStatu(PlayerState.ONLINE);
	}

	// 此处部队信息，应做缓存处理
	public void unLine(int playerId) {
		if (players.containsKey(playerId)) {
			synchronized (players) {
				ArmyProxy army = players.get(playerId);
				army.unload();
				// army.setOnlineStatu(PlayerState.OFFLINE);
				players.remove(playerId);
			}
		}
	}

	public ArmyProxy getArmy(int playerId) {
		if (players.containsKey(playerId)) {
			return players.get(playerId);
		}
		return null;
	}

	public boolean isExist(int playerId) {
		return players.containsKey(playerId);
	}
}
