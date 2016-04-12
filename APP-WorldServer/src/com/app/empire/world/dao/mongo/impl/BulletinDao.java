package com.app.empire.world.dao.mongo.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.Bulletin;

/**
 * 执行与Bulletin表相关数据库操作
 * 
 * @author doter
 */

@Repository
public class BulletinDao extends BaseDao<Bulletin, Integer> {
	@Autowired
	public BulletinDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, Bulletin.class);
	}
	/**
	 * 获取状态开启并且没有结束的公告
	 * 
	 * @param status
	 * @return
	 */
	public List<Bulletin> getBulletins() {
		Query query = new Query();
		query.addCriteria(new Criteria("status").is((byte) 1));
		query.addCriteria(new Criteria("endTime").gt(new Date()));
		return this.mongoTemplate.find(query, Bulletin.class);
	}
	/**
	 * 分页获取公告列表
	 * 
	 * @param id
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page<Bulletin> getBulletinList(int id, int page, int pageSize) {
		Query query = new Query();
		if (id > 0) {
			query.addCriteria(new Criteria("id").is(id));
		}
		PageRequest pageRequest = new PageRequest(page, pageSize, new Sort(Direction.DESC, "id"));
		return findAll(query, pageRequest);
	}

}
