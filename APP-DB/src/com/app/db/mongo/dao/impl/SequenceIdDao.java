package com.app.db.mongo.dao.impl;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.entity.SequenceIdEntity;

@Repository
public class SequenceIdDao extends SimpleMongoRepository<SequenceIdEntity, Serializable> {
	private static Object lock = new Object();// 锁
	private MongoTemplate mongoTemplate;
	private static boolean isNew = true;

	ConcurrentHashMap<String, AtomicInteger> chm = new ConcurrentHashMap<String, AtomicInteger>();
	@Autowired
	public SequenceIdDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory.<SequenceIdEntity, Serializable> getEntityInformation(SequenceIdEntity.class), mongoOperations);
		this.mongoTemplate = mongoOperations;
	}

	// 获取下一个id 值(两个只能同时使用一个，适合独立数据库使用)
	public int getNextSequenceId(String key) {
		AtomicInteger sequence = chm.get(key);
		if (sequence == null) {
			synchronized (lock) {
				sequence = chm.get(key);
				if (sequence == null) {
					SequenceIdEntity sid = findOne(key);
					if (sid == null) {
						sequence = new AtomicInteger(0);
					} else {
						sequence = new AtomicInteger((int) sid.getSeq());
					}
					chm.put(key, sequence);
				}
			}
		}
		int id = sequence.incrementAndGet();
		SequenceIdEntity se = new SequenceIdEntity();
		se.setObjectId(key);
		se.setSeq(id);
		this.save(se);
		return id;
	}

	// 获取下一个id 值(两个只能同时使用一个，适合共用数据库使用)
	public int getNextId(String key) {
		Query query = new Query();
		query.addCriteria(new Criteria("objectId").is(key));
		Update update = new Update();
		update.inc("seq", 1);
		SequenceIdEntity sie;
		int id = 1;
		synchronized (lock) {
			sie = mongoTemplate.findAndModify(query, update, SequenceIdEntity.class);
			if (sie == null) {
				sie = new SequenceIdEntity();
				sie.setObjectId(key);
				sie.setSeq(id);
				this.save(sie);
			} else {
				id = sie.getSeq() + 1;
			}
		}
		return id;
	}
}
