package com.app.empire.world.model.player;

import java.util.HashMap;

import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.entity.mongo.PlayerHeroEquip;

/**
 * 玩家英雄vo
 * 
 * @author doter
 * 
 */
public class PlayerHeroVo {
	private PlayerHero playerHero; // 英雄数据
	private HashMap<Integer, PlayerHeroEquip> playerHeroEquipMap = null;// 英雄装备数据 军阶id->obj

	public PlayerHeroVo(PlayerHero playerHero) {
		this.playerHero = playerHero;
	}
	public PlayerHero getPlayerHero() {
		return playerHero;
	}
	public HashMap<Integer, PlayerHeroEquip> getPlayerHeroEquipMap() {
		return playerHeroEquipMap;
	}
	public void setPlayerHeroEquipMap(HashMap<Integer, PlayerHeroEquip> playerHeroEquipMap) {
		this.playerHeroEquipMap = playerHeroEquipMap;
	}

}
