package com.app.empire.world.dao.mongo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.PlayerTask;

/***
 * 玩家任务dao
 * 
 * @author doter
 */

@Repository
public class PlayerTaskDao extends BaseDao<PlayerTask, Integer> {
	@Autowired
	public PlayerTaskDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerTask.class);
	}
	/** 根据角色id获取任务 */
	public PlayerTask getPlayerListByPlayerId(Integer playerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		return this.mongoTemplate.findOne(query, PlayerTask.class);
	}
	
}
