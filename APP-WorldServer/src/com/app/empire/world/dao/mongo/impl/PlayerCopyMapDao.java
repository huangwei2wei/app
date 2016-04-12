package com.app.empire.world.dao.mongo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.PlayerCopyMap;

@Repository
public class PlayerCopyMapDao extends BaseDao<PlayerCopyMap, Integer> {

	@Autowired
	public PlayerCopyMapDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerCopyMap.class);
	}
	/**
	 * 获取副本列表
	 * 
	 * @param playerId
	 * @param copyType
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<PlayerCopyMap> getCopyList(int playerId, int copyType, int skip, int limit) {
		Query query = new Query();
		query.skip(skip).limit(limit);
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("copyType").is(copyType));
		query.with(new Sort(Direction.ASC, "id"));
		return this.mongoTemplate.find(query, PlayerCopyMap.class);
	}
	/**
	 * 获取玩家指定副本
	 * 
	 * @param playerId
	 * @param copyMapId
	 * @return
	 */
	public PlayerCopyMap getPlayerCopy(int playerId, int copyMapId) {
		Query query = new Query();
		query.addCriteria(new Criteria("copyMapId").is(copyMapId));
		query.addCriteria(new Criteria("playerId").is(playerId));
		return this.mongoTemplate.findOne(query, PlayerCopyMap.class);
	}

}
