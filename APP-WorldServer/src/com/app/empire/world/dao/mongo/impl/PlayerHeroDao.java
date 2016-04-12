package com.app.empire.world.dao.mongo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.model.player.PlayerHeroVo;
import com.app.empire.world.model.player.WorldPlayer;

/**
 * 执行与hero表相关数据库操作
 * 
 * @author doter
 */
@Repository
public class PlayerHeroDao extends BaseDao<PlayerHero, Integer> {
	@Autowired
	public PlayerHeroDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerHero.class);
	}

	/**
	 * 根据玩家角色id获取英雄列表
	 * 
	 * @param playerId
	 * @return
	 */
	public List<PlayerHero> getHeroListByPlayerId(WorldPlayer worldPlayer) {
		ConcurrentHashMap<Integer, PlayerHeroVo> playerHeroMap = worldPlayer.getPlayerHeroMap();
		if (playerHeroMap.size() > 0) {
			List<PlayerHero> PlayerHeroList = new ArrayList<PlayerHero>();
			for (PlayerHeroVo playerHeroVo : playerHeroMap.values()) {
				PlayerHeroList.add(playerHeroVo.getPlayerHero());
			}
			System.out.println(worldPlayer.getPlayer().getNickname() + " 读缓存********");
			return PlayerHeroList;
		}
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
		List<PlayerHero> PlayerHeroList = this.mongoTemplate.find(query, PlayerHero.class);
		for (PlayerHero playerHero : PlayerHeroList) {
			PlayerHeroVo vo = new PlayerHeroVo(playerHero);
			playerHeroMap.put(playerHero.getId(), vo);
		}
		System.out.println(worldPlayer.getPlayer().getNickname() + " 读数据库********");
		return PlayerHeroList;
	}

	/**
	 * 根据玩家角色id和英雄流水id[]获取英雄列表
	 * 
	 * @param playerId
	 * @return
	 */
	public List<PlayerHero> getHeroListByPlayerIdAndHeroId(WorldPlayer worldPlayer, List<Integer> heroId) {
		ConcurrentHashMap<Integer, PlayerHeroVo> playerHeroMap = worldPlayer.getPlayerHeroMap();
		if (playerHeroMap.size() == 0) {
			this.getHeroListByPlayerId(worldPlayer);
		}
		List<PlayerHero> PlayerHeroList = new ArrayList<PlayerHero>();
		for (PlayerHeroVo vo : playerHeroMap.values()) {
			PlayerHero playerHero = vo.getPlayerHero();
			if (heroId.contains(playerHero.getId()))
				PlayerHeroList.add(vo.getPlayerHero());
		}
		return PlayerHeroList;

		// Query query = new Query();
		// query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
		// query.addCriteria(new Criteria("id").in(heroId));
		// return this.mongoTemplate.find(query, PlayerHero.class);
	}

	/**
	 * 根据英雄流水id获取英雄
	 * 
	 * @param playerId
	 * @return
	 */
	public PlayerHero getHeroByHeroId(WorldPlayer worldPlayer, int heroId) {
		ConcurrentHashMap<Integer, PlayerHeroVo> playerHeroMap = worldPlayer.getPlayerHeroMap();
		if (playerHeroMap.size() == 0)
			this.getHeroListByPlayerId(worldPlayer);
		if (!playerHeroMap.containsKey(heroId))
			return null;
		return playerHeroMap.get(heroId).getPlayerHero();

		// Query query = new Query();
		// query.addCriteria(new Criteria("id").is(heroId));
		// query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
		// return this.mongoTemplate.findOne(query, PlayerHero.class);
	}

	/**
	 * 插入英雄必须调用此函数
	 * 
	 * @param worldPlayer
	 * @param playerHero
	 * @return
	 */
	public PlayerHero insert(WorldPlayer worldPlayer, PlayerHero playerHero) {
		ConcurrentHashMap<Integer, PlayerHeroVo> playerHeroMap = worldPlayer.getPlayerHeroMap();
		// if (playerHeroMap.size() == 0)
		// this.getHeroListByPlayerId(worldPlayer);
		super.insert(playerHero);
		PlayerHeroVo vo = new PlayerHeroVo(playerHero);
		playerHeroMap.put(playerHero.getId(), vo);
		return playerHero;
	}

}
