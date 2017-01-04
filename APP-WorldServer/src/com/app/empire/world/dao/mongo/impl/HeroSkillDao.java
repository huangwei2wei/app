package com.app.empire.world.dao.mongo.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.app.db.mongo.dao.impl.BaseDao;
import com.app.empire.world.entity.mongo.HeroSkill;

@Repository
public class HeroSkillDao extends BaseDao<HeroSkill, Integer> {
	@Autowired
	public HeroSkillDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, HeroSkill.class);
	}

	public Map<Integer, HeroSkill> getHeroSkill(int playerId) {
		Map<Integer, HeroSkill> run = new HashMap<Integer, HeroSkill>();
		Query query = new Query();
		query.addCriteria(new Criteria("playerId").is(playerId));
		List<HeroSkill> skills = this.mongoTemplate.find(query, HeroSkill.class);
		for (HeroSkill heroSkill : skills) {
			run.put(heroSkill.getSkillBaseId(), heroSkill);
		}
		return run;
	}
}
