package com.app.empire.world.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.dao.mongo.impl.PlayerCopyMapDao;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mongo.PlayerCopyMap;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.entity.mongo.PlayerTeam;
import com.app.empire.world.entity.mysql.gameConfig.BaseLanguage;
import com.app.empire.world.entity.mysql.gameConfig.MapCopy;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;

/**
 * 副本相关服务
 * 
 * @author doter
 */
@Service
public class PlayerCopyMapService {
	@Autowired
	private PlayerCopyMapDao playerCopyMapDao;
	/**
	 * 获取副本列表
	 * 
	 * @param playerId
	 * @param copyType
	 * @param skip
	 * @param limit
	 * @return
	 */
	public List<PlayerCopyMap> getCopyList(int playerId, int copyType, int skip, int limit) {
		return playerCopyMapDao.getCopyList(playerId, copyType, skip, limit);
	}

	/**
	 * 接取主线副本
	 * 
	 * @param playerId
	 * @param copyMapId 当前副本id
	 * @return
	 */
	@SuppressWarnings({"rawtypes"})
	public PlayerCopyMap acessCopyMap(WorldPlayer worldPlayer, Integer copyMapId) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		Map mapCopy = gameConfigService.getGameConfig().get(MapCopy.class.getSimpleName()).get(copyMapId);
		Map<Integer, Map> lan = gameConfigService.getGameConfig().get(BaseLanguage.class.getSimpleName());
		if (mapCopy == null)
			throw new PlayerDataException(lan.get(11).get("msg").toString());// 副本不存在

		Player player = worldPlayer.getPlayer();
		int preId = Integer.parseInt(mapCopy.get("preId").toString());
		int copyType = Integer.parseInt(mapCopy.get("type").toString());
		int provisionsA = Integer.parseInt(mapCopy.get("provisionsA").toString());// 准入粮草
		int provisionsB = Integer.parseInt(mapCopy.get("provisionsB").toString());// 胜利消耗粮草
		int limitLv = Integer.parseInt(mapCopy.get("userLv").toString());// 限制等级
		if (player.getPower() < provisionsA) // 准入粮草
			throw new PlayerDataException(lan.get(12).get("msg").toString());// 粮草不足
		if (player.getLv() < limitLv)
			throw new PlayerDataException(lan.get(39).get("msg").toString());// 战队等级不足

		// 判断是否可以开启
		if (preId > 0) {
			PlayerCopyMap playerCopyMap = playerCopyMapDao.getPlayerCopy(player.getId(), preId);
			if (playerCopyMap == null || playerCopyMap.getStatus() == 0)
				throw new PlayerDataException(lan.get(36).get("msg").toString());// 关卡未开启
		}
		ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "power", -(provisionsA - provisionsB));
		String dropGoodsStr = CommonUtil.dropGoods(mapCopy.get("drop").toString());
		PlayerCopyMap playerCopyMap = playerCopyMapDao.getPlayerCopy(player.getId(), copyMapId);
		if (playerCopyMap == null) {// 不存在则插入一条记录
			playerCopyMap = new PlayerCopyMap();
			playerCopyMap.setCopyType(copyType);
			playerCopyMap.setPlayerId(player.getId());
			playerCopyMap.setCopyMapId(copyMapId);
			playerCopyMap.setCreateTime(new Date());
			playerCopyMap.setRandomAward(dropGoodsStr);
			playerCopyMap.setStatus(0);
			playerCopyMap.setStar(0);
			playerCopyMapDao.insert(playerCopyMap);
		} else {// 存在则修改
			playerCopyMap.setRandomAward(dropGoodsStr);
			// playerCopyMap.setStatus(0);
			playerCopyMapDao.save(playerCopyMap);
		}
		return playerCopyMap;
	}
	/**
	 * 完成主线副本
	 * 
	 * @param playerId
	 * @param copyMapId
	 * @return
	 */
	public PlayerCopyMap completeCopyMap(WorldPlayer worldPlayer, int copyMapId, int star) throws PlayerDataException {
		Player player = worldPlayer.getPlayer();
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		Map<Integer, Map> lan = gameConfigService.getGameConfig().get(BaseLanguage.class.getSimpleName());
		PlayerCopyMap playerCopyMap = playerCopyMapDao.getPlayerCopy(player.getId(), copyMapId);
		if (playerCopyMap == null)
			throw new PlayerDataException(lan.get(1).get("msg").toString());// 参数错误
		// int status = playerCopyMap.getStatus();
		// if (status == 1)
		// throw new PlayerDataException(lan.get(1).get("msg").toString());// 参数错误
		/* 发物品 */
		String randomAward = playerCopyMap.getRandomAward();
		if (randomAward != null && randomAward.length() > 0) {
			Map<String, List<Object>> awardMap = CommonUtil.strToMap(randomAward);
			List<Map> addGoods = new ArrayList<Map>();
			for (Entry<String, List<Object>> awardMapEntry : awardMap.entrySet()) {
				String k = awardMapEntry.getKey();
				List<Object> v = awardMapEntry.getValue();
				HashMap<String, Integer> goods = new HashMap<String, Integer>();
				goods.put("goodsId", Integer.parseInt(k.toString()));
				goods.put("num", Integer.parseInt(v.get(0).toString()));
				addGoods.add(goods);
			}
			if (addGoods.size() > 0)
				ServiceManager.getManager().getPlayerGoodsService().addGoods(worldPlayer, addGoods);
		}
		/* end发物品 */
		/* 发经验 */
		PlayerTeam playerTeam = ServiceManager.getManager().getPlayerTeamService().getTeam(worldPlayer, 1);
		if (playerTeam != null) {
			Map mapCopy = gameConfigService.getGameConfig().get(MapCopy.class.getSimpleName()).get(copyMapId);
			int heroExperience = Integer.parseInt(mapCopy.get("heroExperience").toString());// 英雄获得经验
			int heroId = playerTeam.getHeroId();
			PlayerHeroService playerHeroService = ServiceManager.getManager().getPlayerHeroService();
			PlayerHero playerHero = playerHeroService.getHeroByHeroId(worldPlayer, heroId);
			playerHeroService.addHeroEXP(worldPlayer, playerHero, heroExperience);
		}
		/* end发经验 */

		/* 更新副本星星数 */
		int oldStar = playerCopyMap.getStar();
		if (star > oldStar) {
			playerCopyMap.setStar(star);
			int playerStarNum = player.getStarNum();
			player.setStarNum(playerStarNum + (star - oldStar));
		}
		/* end更新副本星星数 */
		playerCopyMap.setStatus(1);
		playerCopyMap.setEndTime(new Date());
		playerCopyMapDao.save(playerCopyMap);
		return playerCopyMap;
	}
}
