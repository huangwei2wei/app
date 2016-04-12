package com.app.empire.world.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.common.util.DateUtil;
import com.app.empire.world.dao.mongo.impl.PlayerShopDao;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mongo.PlayerShop;
import com.app.empire.world.entity.mongo.Product;
import com.app.empire.world.entity.mysql.gameConfig.Store;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;

/**
 * 玩家商店
 * 
 * @author doter
 * 
 */

@Service
public class PlayerShopService {
	private Logger log = Logger.getLogger(PlayerShopService.class);
	@Autowired
	private PlayerShopDao playerShopDao;

	/***
	 * 获取玩家商店物品
	 * 
	 * @param worldPlayer
	 * @param shopType 1、神秘商店
	 * @return
	 */
	public PlayerShop getPlayerShop(WorldPlayer worldPlayer, int shopType) {
		int playerId = worldPlayer.getPlayer().getId();
		PlayerShop playerShop = playerShopDao.getShop(playerId, shopType);
		int discount = (int) (7 + Math.random() * (9 - 7 + 1));
		if (playerShop == null) {
			playerShop = new PlayerShop();
			playerShop.setPlayerId(playerId);
			playerShop.setShopType(1);// 类型
			playerShop.setDiscount(discount);// 折扣
			playerShop.setCreateTime(new Date());
			playerShop.setProducts(this.getShopProduct(worldPlayer));
			playerShop.setRefreshNum(new HashMap<String, Integer>());
			playerShopDao.insert(playerShop);
		} else {
			Date createTime = playerShop.getCreateTime();
			if (System.currentTimeMillis() - createTime.getTime() >= 3600000) {// 每两个小时刷新
				playerShop.setDiscount(discount);// 折扣
				playerShop.setCreateTime(new Date());
				playerShop.setProducts(this.getShopProduct(worldPlayer));
				playerShopDao.save(playerShop);
			}
		}
		return playerShop;
	}

	/**
	 * 购买物品
	 * 
	 * @param worldPlayer
	 * @param type 1、神秘商店
	 * @param id 配置表id
	 * @param buyNum 要购买的数量
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	public PlayerShop buyShop(WorldPlayer worldPlayer, int shopType, int id, int buyNum) throws PlayerDataException {
		int playerId = worldPlayer.getPlayer().getId();
		PlayerShop playerShop = playerShopDao.getShop(playerId, shopType);
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		if (playerShop == null)
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		Map<Integer, Product> products = playerShop.getProducts();
		if (products == null)
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		if (!products.containsKey(id))
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		int discount = playerShop.getDiscount();// 折扣
		discount = discount == 0 ? 10 : discount;
		Product product = products.get(id);
		int goodsId = product.getGoodsId();
		int num = product.getNum();
		int gold = product.getGold();
		int diamond = product.getDiamond();
		if (buyNum > num)
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		if (gold > 0) {// 金币大于0 使用金币
			ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "gold", -(int) (gold * discount / 10));
		} else if (diamond > 0) {// 钻石大于0 使用钻石
			ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "diamond", -(int) (diamond * discount / 10));
		}
		// 添加物品
		ArrayList<Map> addGoods = new ArrayList<Map>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("goodsId", goodsId);
		map.put("num", num);
		addGoods.add(map);
		ServiceManager.getManager().getPlayerGoodsService().addGoods(worldPlayer, addGoods);
		// 保存商店数据
		playerShop.setDiscount(0);
		product.setNum(0);
		playerShopDao.save(playerShop);
		return null;
	}

	/**
	 * 手动刷新神秘商店
	 * 
	 * @param worldPlayer
	 */
	public PlayerShop refreshShop(WorldPlayer worldPlayer) throws PlayerDataException {
		Player player = worldPlayer.getPlayer();
		int playerId = player.getId();
		// 判断 钻石
		int vip = player.getVipLv();
		PlayerShop playerShop = playerShopDao.getShop(playerId, 1);
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		if (playerShop == null)
			throw new PlayerDataException(gameConfigService.getMsg(2));// 协议错误
		Map<String, Integer> refreshNumMap = playerShop.getRefreshNum();
		String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
		int num = 0;// 当天刷新次数
		int freeNum = 0;// vip免费次数
		if (refreshNumMap.containsKey(dateStr))
			num = refreshNumMap.get(dateStr);
		num++;
		refreshNumMap.clear();
		refreshNumMap.put(dateStr, num);

		int needDiamond = 0;// 需要钻石
		Map<String, Integer> date1 = (Map<String, Integer>) JSON.parse(gameConfigService.getPar(1));// 刷新次数:消耗钻石数
		Map<String, Integer> date2 = (Map<String, Integer>) JSON.parse(gameConfigService.getPar(2));// VIP等级:免费刷新次数
		if (date2.containsKey(vip + ""))
			freeNum = date2.get(vip + "");

		if (freeNum < num) {
			num -= freeNum;
			if (num > date1.size())
				needDiamond = date1.get(date1.size() + "");
			else
				needDiamond = date1.get(num + "");
			// 扣钻石
			ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "diamond", -needDiamond);
		}

		int discount = (int) (7 + Math.random() * (9 - 7 + 1));
		playerShop.setDiscount(discount);// 折扣
		playerShop.setCreateTime(new Date());
		playerShop.setProducts(this.getShopProduct(worldPlayer));
		playerShopDao.save(playerShop);

		return playerShop;
	}
	/**
	 * 获取随机商店物品
	 * 
	 * @param worldPlayer
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Map<Integer, Product> getShopProduct(WorldPlayer worldPlayer) {
		GameConfigService getGameConfigService = ServiceManager.getManager().getGameConfigService();
		HashMap<String, Map<Integer, Map>> gameConfig = getGameConfigService.getGameConfig();
		Map<Integer, Map> shopGoods = gameConfig.get(Store.class.getSimpleName());// id->map
		Map<String, Double> randomGoods = new HashMap<>();// id->random
		Map<Integer, Map<String, Double>> randomGoods2 = new HashMap<>();// 档次->id->random

		for (Entry<Integer, Map> entry : shopGoods.entrySet()) {
			String id = entry.getKey().toString();
			Double random = Double.parseDouble(entry.getValue().get("random").toString());// 权重
			int grade = Integer.parseInt(entry.getValue().get("grade").toString());// 档次
			randomGoods.put(id, random);
			if (randomGoods2.containsKey(grade)) {
				randomGoods2.get(grade).put(id, random);
			} else {
				Map<String, Double> randomM = new HashMap<>();
				randomM.put(id, random);
				randomGoods2.put(grade, randomM);
			}
		}
		Map<Integer, Product> getGoods = new HashMap<Integer, Product>();
		int scoreTotal = 0;// 评分

		for (Entry<Integer, Map<String, Double>> entry : randomGoods2.entrySet()) {// 每个档次出一个
			int tableId = Integer.parseInt(CommonUtil.randDict(entry.getValue()));// 配置表id
			int score = Integer.parseInt(shopGoods.get(tableId).get("score").toString());// 档次
			int goodsId = Integer.parseInt(shopGoods.get(tableId).get("goodsId").toString());// 物品id
			int num = Integer.parseInt(shopGoods.get(tableId).get("sum").toString());// 物品数量
			int gold = Integer.parseInt(shopGoods.get(tableId).get("costGold").toString());// 物品金币
			int diamond = Integer.parseInt(shopGoods.get(tableId).get("costDiamond").toString());// 物品钻石

			Product product = new Product();
			product.setTableId(tableId);
			product.setGoodsId(goodsId);
			product.setNum(num);
			if (gold == 0 || diamond == 0) {
				product.setGold(gold);
				product.setDiamond(diamond);
			} else {
				if (CommonUtil.random(0.5)) {
					product.setGold(gold);
					product.setDiamond(0);
				} else {
					product.setGold(0);
					product.setDiamond(diamond);
				}
			}
			getGoods.put(tableId, product);
			scoreTotal += score;
		}
		int setScoreTotal = Integer.parseInt(getGameConfigService.getPar(3)); // 神秘商人物品总评分
		while (scoreTotal < setScoreTotal) {// 总评分
			int tableId = Integer.parseInt(CommonUtil.randDict(randomGoods));
			int score = Integer.parseInt(shopGoods.get(tableId).get("score").toString());// 档次
			int goodsId = Integer.parseInt(shopGoods.get(tableId).get("goodsId").toString());// 物品id
			int num = Integer.parseInt(shopGoods.get(tableId).get("sum").toString());// 物品数量
			int gold = Integer.parseInt(shopGoods.get(tableId).get("costGold").toString());// 物品金币
			int diamond = Integer.parseInt(shopGoods.get(tableId).get("costDiamond").toString());// 物品钻石
			Product product = new Product();
			product.setTableId(tableId);
			product.setGoodsId(goodsId);
			product.setNum(num);
			if (gold == 0 || diamond == 0) {
				product.setGold(gold);
				product.setDiamond(diamond);
			} else {
				if (CommonUtil.random(0.5)) {
					product.setGold(gold);
					product.setDiamond(0);
				} else {
					product.setGold(0);
					product.setDiamond(diamond);
				}
			}
			getGoods.put(tableId, product);
			scoreTotal += score;
			if (getGoods.size() >= 12)
				break;
		}
		return getGoods;
	}

}
