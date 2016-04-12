package com.app.db.mongo.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.app.db.mongo.dao.IBaseDao;
import com.app.db.mongo.entity.IEntity;
import com.app.db.mongo.entity.SequenceIdEntity;

@NoRepositoryBean
public abstract class BaseDao<T extends IEntity, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements IBaseDao<T, ID> {

	@Autowired
	SequenceIdDao sequenceIdDao;
	protected MongoTemplate mongoTemplate;
	protected Class<T> clazz;

	private BaseDao(MongoEntityInformation<T, ID> metadata, MongoTemplate mongoOperations) {
		super(metadata, mongoOperations);
	}

	public BaseDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations, Class<T> clazz) {
		this(factory.<T, ID> getEntityInformation(clazz), mongoOperations);
		this.mongoTemplate = mongoOperations;
		this.clazz = clazz;
	}

	@Override
	public <S extends T> S insert(S entity) {
		if (!entity.getClass().getName().equals(SequenceIdEntity.class.getName())) {
			// int id = sequenceIdDao.getNextSequenceId(entity.getClass().getName());
			int id = sequenceIdDao.getNextId(entity.getClass().getName());
			entity.setId(id);
		}
		return super.insert(entity);
	}

	// /**
	// * 分页排序查询
	// * */
	// @Override
	// public List<T> findList(int skip, int limit) {
	// Query query = new Query();
	// if (skip > 0)
	// query.skip(skip);//开始位置
	// if (limit > 0)
	// query.limit(limit);//条数
	//
	// query.addCriteria(new Criteria("id").is(1));//条件
	// Sort s = new Sort(new Order(Sort.Direction.DESC, "test"), new
	// Order(Sort.Direction.DESC, "test1"));//排序
	// query.with(s);
	// return this.mongoTemplate.find(query, this.clazz);
	// }

	/**
	 * 分页排序查询
	 * 
	 * @param query 查询条件
	 * @param pageable 　分页
	 * @return
	 */
	public Page<T> findAll(Query query, final Pageable pageable) {
		long count = this.mongoTemplate.count(query, this.clazz);
		query.with(pageable);
		List<T> list = this.mongoTemplate.find(query, this.clazz);
		return new PageImpl<T>(list, pageable, count);
	}

}
