package com.app.empire.world.entity.mongo;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.app.db.mongo.entity.IEntity;

/**
 * 玩家战队信息
 * 
 * @author doter
 * 
 */

@Document(collection = "player_team")
public class PlayerTeam extends IEntity {

	private int playerId;// 用户id
	private int teamType;// 战队类型1、主线副本
	private int heroId;// 英雄id
	private Map<Integer, Integer> arms;// 兵种id->兵种数量
	private int animalId;// 神兽

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getTeamType() {
		return teamType;
	}
	public void setTeamType(int teamType) {
		this.teamType = teamType;
	}
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}
	public Map<Integer, Integer> getArms() {
		return arms;
	}
	public void setArms(Map<Integer, Integer> arms) {
		this.arms = arms;
	}
	public int getAnimalId() {
		return animalId;
	}
	public void setAnimalId(int animalId) {
		this.animalId = animalId;
	}

}
