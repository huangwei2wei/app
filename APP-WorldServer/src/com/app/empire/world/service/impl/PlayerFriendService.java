package com.app.empire.world.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.dao.mongo.impl.PlayerFriendDao;
import com.app.empire.world.entity.mongo.PlayerFriend;

/**
 * 玩家好友服务
 * 
 * @author doter
 *
 */
@Service
public class PlayerFriendService {
	@Autowired
	private PlayerFriendDao playerFriendDao;

	/**
	 * 获取玩家好友列表
	 * 
	 * @param playerId
	 * @return
	 */
	public List<PlayerFriend> getPlayerFriendList(int playerId) {
		return playerFriendDao.getPlayerFriendList(playerId);
	}
	/**
	 * 获取玩家待通过的申请列表
	 * 
	 * @param playerId
	 * @return
	 */
	public List<PlayerFriend> getPlayerApplyList(int playerId) {
		return playerFriendDao.getPlayerApplyList(playerId);
	}
	
	/**
	 * 获取好有中的技能
	 */
	

}
