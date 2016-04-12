package com.app.empire.world.dao.mongo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;
import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.PlayerTeam;

/**
 * 玩家战队
 * 
 * @author doter
 * 
 */

@Repository
public class PlayerTeamDao extends BaseDao<PlayerTeam, Integer> {
	@Autowired
	public PlayerTeamDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerTeam.class);
	}
	/** 根据副本类型获取副本战队 */
	public PlayerTeam getPlayerTeam(Integer playerId, Integer teamType) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("teamType").is(teamType));
		return this.mongoTemplate.findOne(query, PlayerTeam.class);
	}

}
