package com.app.empire.world.dao.mongo.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;
import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.PlayerHeroEquip;
import com.app.empire.world.model.player.PlayerHeroVo;
import com.app.empire.world.model.player.WorldPlayer;

/**
 * 执行与 player_hero_equip 表相关数据库操作
 */

@Repository
public class PlayerEquipDao extends BaseDao<PlayerHeroEquip, Integer> {
	@Autowired
	public PlayerEquipDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerHeroEquip.class);
	}

	/**
	 * 获取单个英雄所有军衔阶段装备列表
	 * 
	 * @param worldPlayer
	 * @param heroId 英雄流水id
	 * @return
	 */
	public List<PlayerHeroEquip> getHeroEquip(WorldPlayer worldPlayer, int heroId) {
		List<PlayerHeroEquip> playerHeroEquipList = new ArrayList<PlayerHeroEquip>();
		ConcurrentHashMap<Integer, PlayerHeroVo> playerHeroMap = worldPlayer.getPlayerHeroMap();
		if (!playerHeroMap.containsKey(heroId)) {// 英雄无cache
			Query query = new Query();
			query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
			query.addCriteria(new Criteria("heroId").is(heroId));
			return this.mongoTemplate.find(query, PlayerHeroEquip.class);
		}
		PlayerHeroVo vo = playerHeroMap.get(heroId);
		HashMap<Integer, PlayerHeroEquip> playerHeroEquipMap = vo.getPlayerHeroEquipMap();
		if (playerHeroEquipMap == null) {// 需要查询库
			playerHeroEquipMap = new HashMap<Integer, PlayerHeroEquip>();
			vo.setPlayerHeroEquipMap(playerHeroEquipMap);
			Query query = new Query();
			query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
			query.addCriteria(new Criteria("heroId").is(heroId));
			playerHeroEquipList = this.mongoTemplate.find(query, PlayerHeroEquip.class);
			// 加入缓存
			for (PlayerHeroEquip playerHeroEquip : playerHeroEquipList)
				playerHeroEquipMap.put(playerHeroEquip.getRank(), playerHeroEquip);
		} else {
			for (PlayerHeroEquip playerHeroEquip : playerHeroEquipMap.values())
				playerHeroEquipList.add(playerHeroEquip);
		}
		return playerHeroEquipList;

		// Query query = new Query();
		// query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
		// query.addCriteria(new Criteria("heroId").is(heroId));
		// return this.mongoTemplate.find(query, PlayerHeroEquip.class);
	}

	/**
	 * 根据军衔阶段获取装备列表
	 * 
	 * @param playerId
	 * @param heroId
	 * @param rank
	 * @return
	 */
	public PlayerHeroEquip getEquipByRank(WorldPlayer worldPlayer, int heroId, int rank) {
		ConcurrentHashMap<Integer, PlayerHeroVo> playerHeroMap = worldPlayer.getPlayerHeroMap();
		if (!playerHeroMap.containsKey(heroId)) {
			Query query = new Query();
			query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
			query.addCriteria(new Criteria("heroId").is(heroId));
			query.addCriteria(new Criteria("rank").is(rank));
			return this.mongoTemplate.findOne(query, PlayerHeroEquip.class);
		}
		PlayerHeroVo vo = playerHeroMap.get(heroId);
		HashMap<Integer, PlayerHeroEquip> playerHeroEquipMap = vo.getPlayerHeroEquipMap();
		if (playerHeroEquipMap == null) {// 需要查询库
			playerHeroEquipMap = new HashMap<Integer, PlayerHeroEquip>();
			vo.setPlayerHeroEquipMap(playerHeroEquipMap);
			Query query = new Query();
			query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
			query.addCriteria(new Criteria("heroId").is(heroId));
			List<PlayerHeroEquip> playerHeroEquipList = this.mongoTemplate.find(query, PlayerHeroEquip.class);
			// 加入缓存
			for (PlayerHeroEquip playerHeroEquip : playerHeroEquipList)
				playerHeroEquipMap.put(playerHeroEquip.getRank(), playerHeroEquip);
			return playerHeroEquipMap.get(rank);
		}
		return playerHeroEquipMap.get(rank);

		// Query query = new Query();
		// query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
		// query.addCriteria(new Criteria("heroId").is(heroId));
		// query.addCriteria(new Criteria("rank").is(rank));
		// return this.mongoTemplate.findOne(query, PlayerHeroEquip.class);
	}

	/**
	 * 获取所有英雄装备信息
	 * 
	 * @param worldPlayer
	 * @param heroIds 要查询的英雄流水id
	 * @return
	 */
	public List<PlayerHeroEquip> getHeroEquipList(WorldPlayer worldPlayer, List<Integer> heroIds) {
		List<PlayerHeroEquip> run = new ArrayList<PlayerHeroEquip>();
		List<Integer> needQuery = new ArrayList<Integer>();
		ConcurrentHashMap<Integer, PlayerHeroVo> playerHeroMap = worldPlayer.getPlayerHeroMap();
		if (playerHeroMap.isEmpty()) {
			Query query = new Query();
			query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
			query.addCriteria(new Criteria("heroId").in(heroIds));
			return this.mongoTemplate.find(query, PlayerHeroEquip.class);
		}
		for (Integer heroId : heroIds) {
			if (!playerHeroMap.containsKey(heroId))
				continue;
			PlayerHeroVo vo = playerHeroMap.get(heroId);
			HashMap<Integer, PlayerHeroEquip> playerHeroEquipMap = vo.getPlayerHeroEquipMap();
			if (playerHeroEquipMap == null) { // 需要查询库
				needQuery.add(heroId);
				vo.setPlayerHeroEquipMap(new HashMap<Integer, PlayerHeroEquip>());// 设置
			}
		}
		if (needQuery.size() > 0) {// 需要查询
			Query query = new Query();
			query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
			query.addCriteria(new Criteria("heroId").in(needQuery));
			List<PlayerHeroEquip> playerHeroEquipList = this.mongoTemplate.find(query, PlayerHeroEquip.class);
			// 加入缓存
			for (PlayerHeroEquip playerHeroEquip : playerHeroEquipList) {
				int heroId = playerHeroEquip.getHeroId();
				int rank = playerHeroEquip.getRank();
				PlayerHeroVo vo = playerHeroMap.get(heroId);
				HashMap<Integer, PlayerHeroEquip> playerHeroEquipMap = vo.getPlayerHeroEquipMap();
				playerHeroEquipMap.put(rank, playerHeroEquip);
			}
		}
		for (PlayerHeroVo vo : playerHeroMap.values()) {
			int heroId = vo.getPlayerHero().getId();
			if (!heroIds.contains(heroId))
				continue;
			HashMap<Integer, PlayerHeroEquip> playerHeroEquipMap = vo.getPlayerHeroEquipMap();
			run.addAll(playerHeroEquipMap.values());
		}
		return run;

		// Query query = new Query();
		// query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
		// query.addCriteria(new Criteria("heroId").in(heroIds));
		// return this.mongoTemplate.find(query, PlayerHeroEquip.class);
	}

	public PlayerHeroEquip insert(WorldPlayer worldPlayer, PlayerHeroEquip equip) {
		super.insert(equip);
		ConcurrentHashMap<Integer, PlayerHeroVo> playerHeroMap = worldPlayer.getPlayerHeroMap();
		int heroId = equip.getHeroId();
		int rank = equip.getRank();
		if (!playerHeroMap.containsKey(heroId))
			return equip;
		PlayerHeroVo playerHeroVo = playerHeroMap.get(heroId);
		HashMap<Integer, PlayerHeroEquip> playerHeroEquipMap = playerHeroVo.getPlayerHeroEquipMap();
		if (playerHeroEquipMap == null)
			return equip;
		playerHeroEquipMap.put(rank, equip);
		return equip;
	}
}
