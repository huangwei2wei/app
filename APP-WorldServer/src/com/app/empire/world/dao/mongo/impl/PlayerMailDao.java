package com.app.empire.world.dao.mongo.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.PlayerMail;

/**
 * 执行与邮件表相关数据操作
 * 
 * @author doter
 */
@Repository
public class PlayerMailDao extends BaseDao<PlayerMail, Integer> {
	@Autowired
	private RedisTemplate<String, PlayerMail> redisTemplate;

	@Autowired
	public PlayerMailDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerMail.class);
	}
	/** 获取玩家邮件列表 */
	public List<PlayerMail> getMailListByPlayerId(int playerId, int skip, int limit) {
		ListOperations<String, PlayerMail> ops = redisTemplate.opsForList();
		String key = "list_" + playerId + "_" + PlayerMail.class.getSimpleName();
		List<PlayerMail> listCache = ops.range(key, 0, -1);
		// redisTemplate.expire(key, 10000, TimeUnit.SECONDS);
		if (!listCache.isEmpty())
			return listCache;

		Query query = new Query();
		query.skip(skip).limit(limit);
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("isDel").is(0));
		query.with(new Sort(Direction.DESC, "createTime"));
		List<PlayerMail> playerMailList = this.mongoTemplate.find(query, PlayerMail.class);

		ops.rightPushAll(key, playerMailList);
		return playerMailList;
	}
	/** 获取玩家单封邮件 **/
	public PlayerMail getMailById(int playerId, int mailId) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("id").is(mailId));
		return this.mongoTemplate.findOne(query, PlayerMail.class);
	}
	/** 获取玩家指定邮件 **/
	public List<PlayerMail> getMailListById(int playerId, Integer[] mailId) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("id").in((Object[]) mailId));
		return this.mongoTemplate.find(query, PlayerMail.class);
	}
	/** 批量修改玩家邮件状态 **/
	public void playerUpdateMail(int playerId, Integer[] mailId, byte status) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("id").in((Object[]) mailId));
		this.mongoTemplate.updateFirst(query, Update.update("status", status), PlayerMail.class);
	}

	/** 玩家删除邮件 **/
	public void playerDelMail(int playerId, Integer[] mailId) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("id").in((Object[]) mailId));
		this.mongoTemplate.updateFirst(query, Update.update("isDel", (byte) 1), PlayerMail.class);
	}
	/** 删除过期邮件 **/
	public void delMail(Date dateTime) {
		Query query = new Query();
		query.addCriteria(new Criteria("createTime").lte(dateTime));
		query.addCriteria(new Criteria("status").gt((byte) 1));
		this.mongoTemplate.remove(query, PlayerMail.class);
	}

}
