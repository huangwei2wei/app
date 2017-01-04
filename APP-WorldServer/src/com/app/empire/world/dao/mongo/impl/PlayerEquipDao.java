package com.app.empire.world.dao.mongo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.PlayerHeroEquip;
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
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
		query.addCriteria(new Criteria("heroId").is(heroId));
		return this.mongoTemplate.find(query, PlayerHeroEquip.class);
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
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
		query.addCriteria(new Criteria("heroId").is(heroId));
		query.addCriteria(new Criteria("rank").is(rank));
		return this.mongoTemplate.findOne(query, PlayerHeroEquip.class);
	}

	/**
	 * 获取所有英雄装备信息
	 * 
	 * @param worldPlayer
	 * @param heroIds 要查询的英雄流水id
	 * @return
	 */
	public List<PlayerHeroEquip> getHeroEquipList(WorldPlayer worldPlayer, List<Integer> heroIds) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(worldPlayer.getPlayer().getId()));
		query.addCriteria(new Criteria("heroId").in(heroIds));
		return this.mongoTemplate.find(query, PlayerHeroEquip.class);
	}

	public PlayerHeroEquip insert(WorldPlayer worldPlayer, PlayerHeroEquip equip) {
		return super.insert(equip);
	}
}
