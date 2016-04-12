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
import com.app.empire.world.entity.mongo.PlayerGoods;

/**
 * 执行与PlayerGoods表相关数据库操作
 * 
 */
@Repository
public class PlayerGoodsDao extends BaseDao<PlayerGoods, Integer> {
	@Autowired
	public PlayerGoodsDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, PlayerGoods.class);
	}

	/** 获取所有背包物品信息 */
	public List<PlayerGoods> getPlayerGoodsInfo(int playerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		return this.mongoTemplate.find(query, PlayerGoods.class);
	}
	/** 根据物品配置表id获取物品信息 */
	public List<PlayerGoods> getPlayerGoodsListById(int playerId, int goodsId) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("goodsId").is(goodsId));
		query.with(new Sort(Direction.ASC, "goodsNum"));
		return this.mongoTemplate.find(query, PlayerGoods.class);
	}
	/** 根据物品流水id获取物品信息 */
	public PlayerGoods getPlayerGoodsById(int playerId, int id) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("id").is(id));
		return this.mongoTemplate.findOne(query, PlayerGoods.class);
	}
	/** 根据物品id获取没有满的物品信息 */
	public PlayerGoods getGoodsInfoById(int playerId, int goodsId, int max_num) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("goodsId").is(goodsId));
		query.addCriteria(new Criteria("goodsNum").lt(max_num));
		return this.mongoTemplate.findOne(query, PlayerGoods.class);
	}
	/** 根据物品流水id获取物品信息 */
	public List<PlayerGoods> getGoodsInfoByIds(int playerId, Integer[] ids) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("id").in((Object[])ids));
		return this.mongoTemplate.find(query, PlayerGoods.class);
	}
	/** 根据物品流水id删除物品 **/
	public void delGoodsById(int playerId, int id) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		query.addCriteria(new Criteria("id").is(id));
		this.mongoTemplate.remove(query, PlayerGoods.class);
	}
	/** 根据玩家获取背包格子数量 **/
	public int getCount(int playerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		return (int) this.mongoTemplate.count(query, PlayerGoods.class);
	}

}
