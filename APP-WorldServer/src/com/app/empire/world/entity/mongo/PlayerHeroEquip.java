package com.app.empire.world.entity.mongo;

import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Document;
import com.app.db.mongo.entity.IEntity;

/**
 * 玩家英雄装备表
 */

@Document(collection = "player_hero_equip")
public class PlayerHeroEquip extends IEntity {

	private int playerId;// 用户id
	private int heroId;// 英雄流水id
	private int heroType;// 英雄职业
	private int rank; // 当前军衔阶段
	private Map<Integer, HeroEquipGoods> equip;// 栏位id->装备
	private int achieveProAdd; // 装备成就属性加成
	private List<Integer> achieve; // 精炼成就
	private List<Integer> achieve2;// 收集成就

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}
	public int getHeroType() {
		return heroType;
	}
	public void setHeroType(int heroType) {
		this.heroType = heroType;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public Map<Integer, HeroEquipGoods> getEquip() {
		return equip;
	}
	public void setEquip(Map<Integer, HeroEquipGoods> equip) {
		this.equip = equip;
	}
	public int getAchieveProAdd() {
		return achieveProAdd;
	}
	public void setAchieveProAdd(int achieveProAdd) {
		this.achieveProAdd = achieveProAdd;
	}
	public List<Integer> getAchieve() {
		return achieve;
	}
	public void setAchieve(List<Integer> achieve) {
		this.achieve = achieve;
	}
	public List<Integer> getAchieve2() {
		return achieve2;
	}
	public void setAchieve2(List<Integer> achieve2) {
		this.achieve2 = achieve2;
	}

}
