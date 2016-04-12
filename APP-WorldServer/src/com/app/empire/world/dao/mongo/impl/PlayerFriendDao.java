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
import com.app.empire.world.entity.mongo.PlayerFriend;
@Repository
public class PlayerFriendDao extends BaseDao<PlayerFriend, Integer> {

	@Autowired
	public PlayerFriendDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerFriend.class);
	}
	/**
	 * 获取玩家好友列表
	 * 
	 * @param playerId
	 * @return
	 */
	public List<PlayerFriend> getPlayerFriendList(int playerId) {
		Query query = new Query();
		query.addCriteria(new Criteria().orOperator(new Criteria("playerId").is(playerId), new Criteria("targetPId").is(playerId)));
		query.addCriteria(new Criteria("status").is(true));
		query.with(new Sort(Direction.DESC, "dateTime"));
		return this.mongoTemplate.find(query, PlayerFriend.class);
	}
	/**
	 * 获取玩家待通过的申请列表
	 * 
	 * @param playerId
	 * @return
	 */
	public List<PlayerFriend> getPlayerApplyList(int playerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("targetPId").is(playerId));
		query.addCriteria(new Criteria("status").is(false));
		query.with(new Sort(Direction.ASC, "dateTime"));
		return this.mongoTemplate.find(query, PlayerFriend.class);
	}

}
