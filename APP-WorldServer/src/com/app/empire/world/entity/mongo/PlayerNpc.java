package com.app.empire.world.entity.mongo;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.app.db.mongo.entity.IEntity;
/**
 * 玩家 NPC
 * 
 * @author doter
 * 
 */

@Document(collection = "player_npc")
public class PlayerNpc extends IEntity {
	private int playerId;// 玩家角色id
	private Map<Integer, Npc> npc;// 类型 1、金币npc,2、粮草npc ->map

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public Map<Integer, Npc> getNpc() {
		return npc;
	}
	public void setNpc(Map<Integer, Npc> npc) {
		this.npc = npc;
	}

}
