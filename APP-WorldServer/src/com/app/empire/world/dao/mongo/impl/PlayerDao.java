package com.app.empire.world.dao.mongo.impl;

import java.util.ArrayList;
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
import com.app.empire.world.WorldServer;
import com.app.empire.world.entity.mongo.Player;

/**
 * 执行与Player表相关数据库操作
 * 
 * @author doter
 */

@Repository
public class PlayerDao extends BaseDao<Player, Integer> {

	@Autowired
	public PlayerDao(MongoRepositoryFactory factory, MongoTemplate mongoOperations) {
		super(factory, mongoOperations, Player.class);
	}

	/** 根据账号id获取角色列表 */
	public List<Player> getPlayerListByAccountId(Integer accountId) {
		Query query = new Query();
		query.addCriteria(new Criteria("accountId").is(accountId));
		query.addCriteria(new Criteria("serverId").is(WorldServer.serverConfig.getMachineCode()));
		return this.mongoTemplate.find(query, Player.class);
	}

	/** 根据角色id获取角色 */
	public Player getPlayerById(Integer playerId) {
		Query query = new Query();
		query.addCriteria(new Criteria("id").is(playerId));
		return this.mongoTemplate.findOne(query, Player.class);
	}

	/** 根据角色名获取角色 */
	public Player getPlayerByName(Integer accountId, String nickname) {
		Query query = new Query();
		query.addCriteria(new Criteria("accountId").is(accountId));
		query.addCriteria(new Criteria("nickname").is(nickname));
		query.addCriteria(new Criteria("serverId").is(WorldServer.serverConfig.getMachineCode()));
		return this.mongoTemplate.findOne(query, Player.class);
	}

	/** 根据角色名获取角色 */
	public Player getPlayerByName(String nickname) {
		Query query = new Query();
		query.addCriteria(new Criteria("nickname").is(nickname));
		query.addCriteria(new Criteria("serverId").is(WorldServer.serverConfig.getMachineCode()));
		return this.mongoTemplate.findOne(query, Player.class);
	}

	/**
	 * 分页获取角色列表(GM使用)
	 * 
	 * @param user
	 * @param userType
	 * @param regBeginTime
	 * @param regEndTime
	 * @param loginBeginTime
	 * @param loginEndTime
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page<Player> getPlayerList(String user, int userType, long regBeginTime, long regEndTime, long loginBeginTime, long loginEndTime, int page, int pageSize) {
		Query query = new Query();
		if (userType > -1 && user.length() > 0) {// 0玩家ID，1玩家账号，2玩家昵称
			if (userType == 0) {
				query.addCriteria(new Criteria("id").is(Integer.parseInt(user)));
			} else if (userType == 1) {
				query.addCriteria(new Criteria("accountName").is(user));
			} else if (userType == 2) {
				query.addCriteria(new Criteria("nickname").is(user));
			}
		}
		ArrayList<Criteria> criteriaList = new ArrayList<Criteria>();
		if (regBeginTime > -1) {
			criteriaList.add(new Criteria("createTime").gte(new Date(regBeginTime * 1000)));
		}
		if (regEndTime > -1) {
			criteriaList.add(new Criteria("createTime").lte(new Date(regEndTime * 1000)));
		}
		if (loginBeginTime > -1) {
			criteriaList.add(new Criteria("loginTime").gte(new Date(loginBeginTime * 1000)));
		}
		if (loginEndTime > -1) {
			criteriaList.add(new Criteria("loginTime").lte(new Date(loginEndTime * 1000)));
		}
		if (criteriaList.size() > 0) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
		}
		PageRequest pageRequest = new PageRequest(page, pageSize, new Sort(Direction.DESC, "id"));
		return findAll(query, pageRequest);
	}
	/**
	 * 根据账号类型获取玩家列表（GM使用）
	 * 
	 * @param user 帐号名，或者ID，或者昵称 多个用户用 “,” 英文逗号隔开
	 * @param userType 0玩家ID，1玩家账号，2玩家昵称
	 * @return
	 */
	public List<Player> getPlayerList(List<String> user, int userType) {
		Query query = new Query();
		if (userType == 0) {
			List<Integer> userIds = new ArrayList<Integer>();
			for (String userid : user) {
				userIds.add(Integer.parseInt(userid));
			}
			query.addCriteria(new Criteria("id").in(userIds));
		} else if (userType == 1) {
			query.addCriteria(new Criteria("accountName").in(user));
		} else if (userType == 2) {
			query.addCriteria(new Criteria("nickname").in(user));
		}
		return this.mongoTemplate.find(query, Player.class);
	}

}
