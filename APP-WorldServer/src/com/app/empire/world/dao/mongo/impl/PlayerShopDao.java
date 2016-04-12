package com.app.empire.world.dao.mongo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.PlayerShop;

/**
 * 商店
 * 
 * @author doter
 * 
 */
@Repository
public class PlayerShopDao extends BaseDao<PlayerShop, Integer> {
	@Autowired
	public PlayerShopDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerShop.class);
	}

	/**
	 * 获取商店
	 * 
	 * @param playerId 玩家角色id
	 * @param shopType 1、神秘商人
	 * @return
	 */
	public PlayerShop getShop(int playerId, int shopType) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("shopType").is(shopType));
		return this.mongoTemplate.findOne(query, PlayerShop.class);
	}

}
