package com.app.empire.world.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.dao.mongo.impl.PlayerEquipDao;
import com.app.empire.world.entity.mongo.HeroEquipGoods;
import com.app.empire.world.entity.mongo.PlayerGoods;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.entity.mongo.PlayerHeroEquip;
import com.app.empire.world.entity.mysql.gameConfig.EquipAchieve;
import com.app.empire.world.entity.mysql.gameConfig.EquipBar;
import com.app.empire.world.entity.mysql.gameConfig.EquipRefine;
import com.app.empire.world.entity.mysql.gameConfig.Goods;
import com.app.empire.world.entity.mysql.gameConfig.Hero;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;
/**
 * EquipService 处理与装备相关操作业务逻辑层
 */

@Service
public class PlayerEquipService {
	private Logger log = Logger.getLogger(PlayerEquipService.class);
	@Autowired
	private PlayerEquipDao equipDao;

	/* 获取装备信息 */
	public List<PlayerHeroEquip> getHeroEquipList(WorldPlayer worldPlayer, List<Integer> heroIds) {
		return equipDao.getHeroEquipList(worldPlayer, heroIds);
	}

	/**
	 * 穿戴装备
	 * 
	 * @param worldPlayer
	 * @param goodsId 物品流水id
	 * @param heroId 游戏流水id
	 * @param rank 军阶
	 * @param equipNo 装备栏编号
	 * @return
	 * @throws PlayerDataException
	 */
	@SuppressWarnings("rawtypes")
	public PlayerHeroEquip wearEquip(WorldPlayer worldPlayer, int goodsId, int heroId, int rank, int equipNo) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		int playerId = worldPlayer.getPlayer().getId();
		// 查找此装备栏中的物品是否存在
		PlayerGoods playerGoods = ServiceManager.getManager().getPlayerGoodsService().getPlayerGoodsById(playerId, goodsId);
		if (playerGoods == null) {
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		}
		// 获取英雄职业
		Map<Integer, Map> baseHeroMap = gameConfigService.getGameConfig().get(Hero.class.getSimpleName());
		PlayerHero playerHero = ServiceManager.getManager().getPlayerHeroService().getHeroByHeroId(worldPlayer, heroId);
		if (playerHero == null) {
			throw new PlayerDataException(gameConfigService.getMsg(10));// 英雄不存在
		}
		Map baseHero = baseHeroMap.get(playerHero.getHeroBaseId());// 英雄基表配置
		Map<String, Map> equipBar = gameConfigService.getGameConfig4MulKey().get(EquipBar.class.getSimpleName());// 装备栏配置表
		// 获取英雄装备信息
		PlayerHeroEquip playerHeroEquip = equipDao.getEquipByRank(worldPlayer, heroId, rank);
		Map<Integer, HeroEquipGoods> equip = new HashMap<Integer, HeroEquipGoods>();
		if (playerHeroEquip != null) {
			equip = playerHeroEquip.getEquip();
			HeroEquipGoods heroEquipGoods = equip.get(equipNo);
			if (heroEquipGoods != null) {
				throw new PlayerDataException(gameConfigService.getMsg(21));// 装备栏已经存在装备
			}
		}
		String equipBarKey = baseHero.get("heroType").toString() + "_" + rank;// 索引
		Map equipBarEntry = equipBar.get(equipBarKey);// 装备栏配置
		if (equipBarEntry == null)
			throw new PlayerDataException(gameConfigService.getMsg(22));// 缺少配置
		if (equipBarEntry.get("equip" + equipNo) == null)
			throw new PlayerDataException(gameConfigService.getMsg(23));// 不能穿戴
		String strEquip = equipBarEntry.get("equip" + equipNo).toString();
		String[] str = strEquip.split(":");
		int equipId = Integer.parseInt(str[0]);// 装备id
		int openLv = Integer.parseInt(str[1]);// 穿戴等级
		if (equipId != playerGoods.getGoodsId())
			throw new PlayerDataException(gameConfigService.getMsg(24));// 装备栏对应装备不正确
		if (playerHero.getLv() < openLv)
			throw new PlayerDataException(gameConfigService.getMsg(25));// 装备栏未达到开启等级

		HeroEquipGoods goods = new HeroEquipGoods();
		goods.setGoodsId(playerGoods.getGoodsId());
		goods.setGoodsExp(0);
		goods.setProAdd(0);
		goods.setGoodsQuality(playerGoods.getGoodsQuality());
		goods.setGoodsStar(playerGoods.getGoodsStar());
		goods.setProperty(playerGoods.getProperty());
		equip.put(equipNo, goods);// 设置装备

		if (playerHeroEquip != null) {
			equipDao.save(playerHeroEquip);
		} else {
			PlayerHeroEquip newEquip = new PlayerHeroEquip();
			newEquip.setHeroId(heroId);
			newEquip.setHeroType(Integer.parseInt(baseHero.get("heroType").toString()));
			newEquip.setPlayerId(playerId);
			newEquip.setRank(rank);
			newEquip.setEquip(equip);
			newEquip.setAchieveProAdd(0);
			this.equipDao.insert(worldPlayer, newEquip);
		}
		ServiceManager.getManager().getForceCalculateService().statisticsProperty(worldPlayer, playerHero);// 计算英雄属性战力
		// 删除背包装备
		ServiceManager.getManager().getPlayerGoodsService().delPlayerGoods(worldPlayer, playerGoods, 1);
		worldPlayer.pushUpdateGoods();
		// 更新属性
		return playerHeroEquip;
	}

	/**
	 * 合成装备并穿戴
	 * 
	 * @param player 玩家
	 * @param heroId 英雄流水id
	 * @param rank 军阶
	 * @param equipNo 栏位号
	 * @param goodsId 要合成的物品id
	 * @return
	 * @throws PlayerDataException
	 */
	@SuppressWarnings("rawtypes")
	public boolean mergeAndWearEquip(WorldPlayer worldPlayer, int heroId, int rank, int equipNo, int goodsId) throws PlayerDataException {
		ServiceManager serviceManager = ServiceManager.getManager();
		GameConfigService gameConfigService = serviceManager.getGameConfigService();
		Map<String, Map> equipBar = gameConfigService.getGameConfig4MulKey().get(EquipBar.class.getSimpleName());
		// 获取英雄职业
		PlayerHero playerHero = ServiceManager.getManager().getPlayerHeroService().getHeroByHeroId(worldPlayer, heroId);
		if (playerHero == null)
			throw new PlayerDataException(gameConfigService.getMsg(10));// 英雄不存在
		Map<Integer, Map> baseHero = gameConfigService.getGameConfig().get(Hero.class.getSimpleName());
		Map hero = baseHero.get(playerHero.getHeroBaseId());
		String heroType = hero.get("heroType").toString();
		Map equipBarEntry = equipBar.get(heroType + "_" + rank);
		if (equipBarEntry == null)
			throw new PlayerDataException(gameConfigService.getMsg(22));// 缺少配置
		String strEquip = equipBarEntry.get("equip" + equipNo).toString();
		String[] str = strEquip.split(":");
		int equipId = Integer.parseInt(str[0]);
		int openLv = Integer.parseInt(str[1]);
		if (equipId != goodsId)
			throw new PlayerDataException(gameConfigService.getMsg(24));// 装备栏对应装备不正确
		if (playerHero.getLv() < openLv)
			throw new PlayerDataException(gameConfigService.getMsg(25));// 装备栏未达到开启等级

		// 检测是否已经装备
		PlayerHeroEquip playerHeroEquip = equipDao.getEquipByRank(worldPlayer, heroId, rank);
		if (playerHeroEquip != null) {
			Map<Integer, HeroEquipGoods> equip = playerHeroEquip.getEquip();
			HeroEquipGoods heroEquipGoods = equip.get(equipNo);
			if (heroEquipGoods != null)
				throw new PlayerDataException(gameConfigService.getMsg(21));// 装备栏已经存在装备
		}
		HeroEquipGoods heroEquipGoods = this.mergeEquip(worldPlayer, goodsId);// 合成物品
		// 新装备并穿戴
		if (playerHeroEquip == null) {
			Map<Integer, HeroEquipGoods> equip = new HashMap<Integer, HeroEquipGoods>();
			equip.put(equipNo, heroEquipGoods);
			PlayerHeroEquip newEquip = new PlayerHeroEquip();
			newEquip.setHeroId(heroId);
			newEquip.setHeroType(Integer.parseInt(heroType));
			newEquip.setPlayerId(worldPlayer.getPlayer().getId());
			newEquip.setRank(rank);
			newEquip.setEquip(equip);
			newEquip.setAchieveProAdd(0);
			this.equipDao.insert(worldPlayer, newEquip);
		} else {
			playerHeroEquip.getEquip().put(equipNo, heroEquipGoods);
			this.equipDao.save(playerHeroEquip);
		}
		ServiceManager.getManager().getForceCalculateService().statisticsProperty(worldPlayer, playerHero);// 计算英雄属性战力
		return true;
	}

	/**
	 * 合成物品
	 * 
	 * @param worldPlayer
	 * @param heroId
	 * @param rank
	 * @param equipNo
	 * @param goodsId
	 * @return
	 * @throws PlayerDataException
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private HeroEquipGoods mergeEquip(WorldPlayer worldPlayer, int goodsId) throws PlayerDataException {
		ServiceManager serviceManager = ServiceManager.getManager();
		GameConfigService gameConfigService = serviceManager.getGameConfigService();
		Map<Integer, Map> baseGoods = gameConfigService.getGameConfig().get(Goods.class.getSimpleName());
		// 获取装备合成材料
		Map goodsMap = baseGoods.get(goodsId);
		String syntStr = goodsMap.get("syntAndSum").toString();
		int syntGold = Integer.parseInt(goodsMap.get("syntGold").toString());// 消耗金币
		Map<String, List<Object>> syntMap = CommonUtil.strToMap(syntStr);// 合成需要的物品信息
		for (Entry<String, List<Object>> entry : syntMap.entrySet()) {
			int needGoodsId = Integer.parseInt(entry.getKey().toString());
			int needNum = Integer.parseInt(entry.getValue().get(0).toString());
			int goodsNum = serviceManager.getPlayerGoodsService().getPlayerGoodsNum(worldPlayer, needGoodsId);
			if (goodsNum < needNum) {
				throw new PlayerDataException(gameConfigService.getMsg(26));// 装备合成材料不足
			}
		}
		serviceManager.getPlayerService().addPlayerMoney(worldPlayer, "gold", -syntGold);
		// 删除材料
		for (Entry<String, List<Object>> entry : syntMap.entrySet()) {
			int needGoodsId = Integer.parseInt(entry.getKey().toString());
			int needNum = Integer.parseInt(entry.getValue().get(0).toString());
			serviceManager.getPlayerGoodsService().delPlayerGoodsByGoodsId(worldPlayer, needGoodsId, needNum);
		}
		worldPlayer.pushUpdateGoods();
		// 生成一个 HeroEquipGoods
		HeroEquipGoods heroEquipGoods = new HeroEquipGoods();
		heroEquipGoods.setGoodsId(goodsId);
		heroEquipGoods.setGoodsExp(0);
		heroEquipGoods.setGoodsQuality(Integer.parseInt(goodsMap.get("quality").toString()));
		heroEquipGoods.setGoodsStar(0);
		heroEquipGoods.setProAdd(0);
		HashMap baseProperty = new HashMap();
		baseProperty.put("aa", Arrays.asList(goodsMap.get("aa").toString()));
		baseProperty.put("ab", Arrays.asList(goodsMap.get("ab").toString()));
		baseProperty.put("ac", Arrays.asList(goodsMap.get("ac").toString()));
		baseProperty.put("ad", Arrays.asList(goodsMap.get("ad").toString()));
		baseProperty.put("ae", Arrays.asList(goodsMap.get("ae").toString()));
		baseProperty.put("af", Arrays.asList(goodsMap.get("af").toString()));
		baseProperty.put("ag", Arrays.asList(goodsMap.get("ag").toString()));
		baseProperty.put("ah", Arrays.asList(goodsMap.get("ah").toString()));
		baseProperty.put("ai", Arrays.asList(goodsMap.get("ai").toString()));
		baseProperty.put("aj", Arrays.asList(goodsMap.get("aj").toString()));
		baseProperty.put("ak", Arrays.asList(goodsMap.get("ak").toString()));
		baseProperty.put("al", Arrays.asList(goodsMap.get("al").toString()));
		baseProperty.put("am", Arrays.asList(goodsMap.get("am").toString()));
		baseProperty.put("an", Arrays.asList(goodsMap.get("an").toString()));
		baseProperty.put("ao", Arrays.asList(goodsMap.get("ao").toString()));
		baseProperty.put("ap", Arrays.asList(goodsMap.get("ap").toString()));
		baseProperty.put("aq", Arrays.asList(goodsMap.get("aq").toString()));
		baseProperty.put("ar", Arrays.asList(goodsMap.get("ar").toString()));
		heroEquipGoods.setProperty(CommonUtil.mapToStr(baseProperty));
		return heroEquipGoods;
	}

	/**
	 * 装备精炼
	 * 
	 * @param player
	 * @param heroId 英雄流水id
	 * @param rank
	 * @param equipNo 要精炼的装备所在的栏位号
	 * @param goodsid 玩家物品流水id
	 * @param goodsNum 数量
	 * @return
	 * @throws PlayerDataException
	 */
	@SuppressWarnings("rawtypes")
	public PlayerHeroEquip refineEquip(WorldPlayer worldPlayer, int heroId, int rank, int equipNo, Integer[] goodsIds, int[] goodsNums) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		Map<String, Map> equipRefine = gameConfigService.getGameConfig4MulKey().get(EquipRefine.class.getSimpleName());
		Map<Integer, Map> baseGoods = gameConfigService.getGameConfig().get(Goods.class.getSimpleName());
		// 装备是否可精炼
		if (goodsIds.length != goodsNums.length) {
			throw new PlayerDataException(gameConfigService.getMsg(1));// 参数错误
		}
		PlayerHeroEquip heroEquip = equipDao.getEquipByRank(worldPlayer, heroId, rank);
		if (heroEquip == null)
			throw new PlayerDataException(gameConfigService.getMsg(27));// 装备栏没有装备

		HeroEquipGoods heroEquipGoods = heroEquip.getEquip().get(equipNo);// 栏位号上的装备
		if (heroEquipGoods == null)
			throw new PlayerDataException(gameConfigService.getMsg(27));// 装备栏没有装备
		int quality = heroEquipGoods.getGoodsQuality();// 物品品质
		int star = heroEquipGoods.getGoodsStar();// 物品当前星数
		// if (quality == 0) // 不能精炼
		// throw new PlayerDataException(gameConfigService.getMsg(28));// 当前品质不能精炼
		// if ((quality == 1 && star >= 1) || (quality == 2 && star >= 3) || (quality == 3 && star >= 5) || (quality == 4 && star >= 5)) // 精炼星级已满
		// throw new PlayerDataException(gameConfigService.getMsg(30));// 精炼以至最高级

		int exp = heroEquipGoods.getGoodsExp();// 当前经验
		int proAdd = heroEquipGoods.getProAdd();

		// 背包物品是否可做精炼材料
		List<PlayerGoods> playerGoodsList = ServiceManager.getManager().getPlayerGoodsService().getGoodsInfoByIds(worldPlayer.getPlayer().getId(), goodsIds);
		if (playerGoodsList.size() != goodsIds.length)
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		Map<Integer, Integer> useGoods = new HashMap<Integer, Integer>();// 物品id -> 数量
		for (int i = 0; i < goodsIds.length; i++) {
			useGoods.put(goodsIds[i], goodsNums[i]);
		}

		for (PlayerGoods goods : playerGoodsList) {
			int goodsId = goods.getId();
			int num = useGoods.get(goodsId);
			if (num > goods.getGoodsNum())
				throw new PlayerDataException(gameConfigService.getMsg(14));// 物品数量不足
			int refineGetExp = Integer.parseInt(baseGoods.get(goods.getGoodsId()).get("refineGetExp").toString());// 装备可获得的精炼经验值
			if (refineGetExp == 0)
				throw new PlayerDataException(gameConfigService.getMsg(29));// 装备精炼材料错误
			exp += refineGetExp * num;
		}

		// 升级计算
		while (true) {
			String equipRefineKey = quality + "_" + (star);
			Map equipRefineMap = equipRefine.get(equipRefineKey);// 经验配置
			if (equipRefineMap == null)
				throw new PlayerDataException(gameConfigService.getMsg(30));// 精炼以至最高级
			int needExp = Integer.parseInt(equipRefineMap.get("exp").toString());// 需要的升级经验
			if (needExp > 0) {
				if (exp < needExp)// 不够升级
					break;
				proAdd = Integer.parseInt(equipRefineMap.get("proAdd").toString());
				exp -= needExp;
				star++;
			} else {
				exp = 0;
				break;
			}
		}

		heroEquipGoods.setGoodsStar(star);
		heroEquipGoods.setGoodsExp(exp);
		heroEquipGoods.setProAdd(proAdd);
		this.equipDao.save(heroEquip);
		// 删除精炼材料
		for (PlayerGoods playerGoods : playerGoodsList) {
			ServiceManager.getManager().getPlayerGoodsService().delPlayerGoods(worldPlayer, playerGoods, useGoods.get(playerGoods.getId()));
		}
		// for (int i = 0; i < goodsIds.length; i++) {
		// ServiceManager.getManager().getPlayerGoodsService().delPlayerGoods(worldPlayer, goodsIds[i], goodsNums[i]);
		// }
		worldPlayer.pushUpdateGoods();
		return heroEquip;
	}
	/**
	 * 装备成就激活
	 * 
	 * @param player
	 * @param heroId
	 * @param rank
	 * @param type 1:精炼成就，2：收集成就
	 * @throws PlayerDataException
	 */
	@SuppressWarnings("rawtypes")
	public PlayerHeroEquip Activate(WorldPlayer worldPlayer, int heroId, int achieveId) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		Map<Integer, Map> equipAchieve = gameConfigService.getGameConfig().get(EquipAchieve.class.getSimpleName());
		Map equipAchieveEntry = equipAchieve.get(achieveId);
		if (equipAchieveEntry == null)
			throw new PlayerDataException(gameConfigService.getMsg(1));// 参数错误
		int rank = Integer.parseInt(equipAchieveEntry.get("rank").toString());// 军阶
		Integer achieveType = Integer.parseInt(equipAchieveEntry.get("achieveType").toString());// 成就类型

		PlayerHeroEquip heroEquip = equipDao.getEquipByRank(worldPlayer, heroId, rank);
		if (heroEquip == null)
			throw new PlayerDataException(gameConfigService.getMsg(27));// 装备栏没有装备

		Map<Integer, HeroEquipGoods> heroEquipGoods = heroEquip.getEquip();
		int haveNum = 0;
		List<Integer> achieve = null;
		if (achieveType == 1) {
			for (Entry<Integer, HeroEquipGoods> entry : heroEquipGoods.entrySet()) {
				HeroEquipGoods heroEquipGood = entry.getValue();
				if (heroEquipGood.getGoodsStar() > 0)
					haveNum++;
			}
			achieve = heroEquip.getAchieve(); // 精炼成就
		} else if (achieveType == 2) {
			haveNum = heroEquipGoods.size();
			achieve = heroEquip.getAchieve2(); // 收集成就
		}
		if (achieve == null)
			achieve = new ArrayList<Integer>();

		Integer num = Integer.parseInt(equipAchieveEntry.get("num").toString());// 需要的数量
		if (haveNum == 0 || haveNum < num)
			throw new PlayerDataException(gameConfigService.getMsg(35));// 装备条件不足
		Integer proAdd = Integer.parseInt(equipAchieveEntry.get("proAdd").toString());
		if (achieve.contains(achieveId))
			throw new PlayerDataException(gameConfigService.getMsg(31));// 成就已经激活
		achieve.add(achieveId);

		if (achieveType == 1)
			heroEquip.setAchieve(achieve);
		else if (achieveType == 2)
			heroEquip.setAchieve2(achieve);

		heroEquip.setAchieveProAdd(heroEquip.getAchieveProAdd() + proAdd);
		equipDao.save(heroEquip);
		return heroEquip;
	}
}
