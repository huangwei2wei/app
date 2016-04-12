package com.app.empire.world.dao.mongo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.PlayerNpc;

/**
 * 玩家npc
 * 
 * @author doter
 * 
 */

@Repository
public class PlayerNpcDao extends BaseDao<PlayerNpc, Integer> {

	@Autowired
	public PlayerNpcDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerNpc.class);
	}

	/**
	 * 获取玩家 NPC
	 * 
	 * @param playerId 玩家角色id
	 * @return
	 */
	public PlayerNpc getNpc(int playerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		return this.mongoTemplate.findOne(query, PlayerNpc.class);
	}

}
