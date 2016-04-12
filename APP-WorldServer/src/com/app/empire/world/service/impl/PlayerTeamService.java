package com.app.empire.world.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.protocol.data.copymap.SaveTeam;
import com.app.empire.world.dao.mongo.impl.PlayerTeamDao;
import com.app.empire.world.entity.mongo.PlayerTeam;
import com.app.empire.world.model.player.WorldPlayer;

/**
 * 玩家战队服务
 * 
 * @author doter
 * 
 */
@Service
public class PlayerTeamService {
	private Logger log = Logger.getLogger(PlayerTeamService.class);
	@Autowired
	PlayerTeamDao playerTeamDao;

	/**
	 * 战队保存
	 */
	public PlayerTeam saveTeam(WorldPlayer worldPlayer, SaveTeam saveTeam) {
		int playerId = worldPlayer.getPlayer().getId();
		int teamType = saveTeam.getTeamType();// 类型1、主线副本
		int heroId = saveTeam.getHeroId();// 英雄id
		int[] arms = saveTeam.getArms();// 兵种
		int[] ArmsNum = saveTeam.getArmsNum();// 兵种数量
		int animalId = saveTeam.getAnimalId();// 神兽
		PlayerTeam playerTeam = playerTeamDao.getPlayerTeam(playerId, teamType);
		Map<Integer, Integer> armsInfo = new HashMap<Integer, Integer>();
		for (int i = 0; i < arms.length; i++) {
			armsInfo.put(arms[i], ArmsNum[i]);
		}
		if (playerTeam == null) {
			playerTeam = new PlayerTeam();
			playerTeam.setPlayerId(playerId);
			playerTeam.setTeamType(teamType);
			playerTeam.setHeroId(heroId);
			playerTeam.setArms(armsInfo);
			playerTeam.setAnimalId(animalId);
			playerTeamDao.insert(playerTeam);
		} else {
			playerTeam.setPlayerId(playerId);
			playerTeam.setTeamType(teamType);
			playerTeam.setHeroId(heroId);
			playerTeam.setArms(armsInfo);
			playerTeam.setAnimalId(animalId);
			playerTeamDao.save(playerTeam);
		}
		return playerTeam;
	}

	/**
	 * 获取战队
	 * 
	 * @param worldPlayer
	 * @param teamType 战队类型 1、主线副本
	 * @return
	 */
	public PlayerTeam getTeam(WorldPlayer worldPlayer, int teamType) {
		int playerId = worldPlayer.getPlayer().getId();
		PlayerTeam playerTeam = playerTeamDao.getPlayerTeam(playerId, teamType);
		return playerTeam;
	}

}
