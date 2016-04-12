package com.app.empire.world.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.app.empire.protocol.data.backpack.UpdateGoodsData;
import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.dao.mongo.impl.PlayerGoodsDao;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mongo.PlayerGoods;
import com.app.empire.world.entity.mysql.gameConfig.BaseLanguage;
import com.app.empire.world.entity.mysql.gameConfig.Box;
import com.app.empire.world.entity.mysql.gameConfig.Goods;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;
/**
 * 处理与物品相关操作业务逻辑层
 */

@Service
@SuppressWarnings("unused")
public class PlayerGoodsService {
	private Logger log = Logger.getLogger(PlayerGoodsService.class);
	@Autowired
	private PlayerGoodsDao playerGoodsDao;
	private final int MAX_SLOT_NUM = 300; // 最大背包格子数量

	/**
	 * 获取背包物品列表
	 */
	public List<PlayerGoods> getBackpackList(WorldPlayer player) {
		return playerGoodsDao.getPlayerGoodsInfo(player.getPlayer().getId());
	}
	/** 根据物品流水id获取物品信息 */
	public PlayerGoods getPlayerGoodsById(int playerId, int id) {
		return playerGoodsDao.getPlayerGoodsById(playerId, id);
	}
	/**
	 * 添加物品(包含资源)
	 * 
	 * @param player
	 * @param addGoods [{"goodsId":100,"num":1},{"goodsId":101,"num":1}]
	 * @throws Exception
	 */

	public void addGoods(WorldPlayer worldPlayer, List<Map> addGoods) throws PlayerDataException {
		PlayerService PlayerService = ServiceManager.getManager().getPlayerService();
		for (Map<String, Integer> goods : addGoods) {
			if (goods.containsKey("goodsId")) { // 物品
				addPlayerGoods(worldPlayer, goods.get("goodsId"), goods.get("num"));
			} else if (goods.containsKey("teamExp")) { // 玩家角色经验
				PlayerService.addPlayerEXP(worldPlayer, goods.get("teamExp"));
			} else if (goods.containsKey("gold")) { // 玩家金币
				PlayerService.addPlayerMoney(worldPlayer, "gold", goods.get("gold"));
			} else if (goods.containsKey("diamond")) { // 钻石
				PlayerService.addPlayerMoney(worldPlayer, "diamond", goods.get("diamond"));
			} else if (goods.containsKey("power")) { // 粮草
				PlayerService.addPlayerMoney(worldPlayer, "power", goods.get("power"));
			}
		}
		worldPlayer.pushUpdateGoods();
	}
	/**
	 * 使用消耗品
	 * 
	 * @param player
	 * @param id 物品流水id
	 * @param goodsNum 物品数量
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public ArrayList<HashMap<String, Integer>> useGoods(WorldPlayer worldPlayer, int id, int goodsNum) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		PlayerGoods playerGoods = playerGoodsDao.getPlayerGoodsById(worldPlayer.getPlayer().getId(), id);
		if (playerGoods == null)
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在

		int nowGoodsNum = playerGoods.getGoodsNum();
		if (nowGoodsNum < goodsNum)
			throw new PlayerDataException(gameConfigService.getMsg(14));// 物品数量不足

		Map<Integer, Map> baseGoods = gameConfigService.getGameConfig().get(Goods.class.getSimpleName());
		Map goods = baseGoods.get(playerGoods.getGoodsId());
		if (Integer.parseInt(goods.get("type").toString()) != 2 || Integer.parseInt(goods.get("canUse").toString()) == 0)
			throw new PlayerDataException(gameConfigService.getMsg(15));// 物品不能使用

		// 获取使用效果
		String useEffect = goods.get("usingEffect").toString();
		if (useEffect == null || useEffect.toString().equals(""))
			throw new PlayerDataException(gameConfigService.getMsg(15));// 物品不能使用

		Map<String, Integer> useEff = (Map<String, Integer>) JSON.parse(useEffect);
		if (useEff == null || useEff.size() == 0)
			throw new PlayerDataException(gameConfigService.getMsg(16));// 配置错误

		// 判断背包是否达到最大
		if (isPackFull(worldPlayer))
			throw new PlayerDataException(gameConfigService.getMsg(17));// 背包已满
		// 修改物品数量
		if (nowGoodsNum - goodsNum > 0) {
			playerGoods.setGoodsNum(nowGoodsNum - goodsNum);
			playerGoodsDao.save(playerGoods);
		} else {
			playerGoods.setGoodsNum(0);
			playerGoodsDao.delete(playerGoods);
		}
		ArrayList<HashMap<String, Integer>> runData = new ArrayList<HashMap<String, Integer>>();
		if (useEff.containsKey("teamExp")) {// 添加角色经验
			int teamExp = useEff.get("teamExp") * goodsNum;// 获得的经验
			ServiceManager.getManager().getPlayerService().addPlayerEXP(worldPlayer, teamExp);
			HashMap<String, Integer> run = new HashMap<String, Integer>();
			run.put("teamExp", teamExp);
			runData.add(run);
		} else if (useEff.containsKey("gold")) {// 添加金币
			int gold = useEff.get("gold") * goodsNum;// 获得的金币
			ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "gold", gold);
			HashMap<String, Integer> run = new HashMap<String, Integer>();
			run.put("gold", gold);
			runData.add(run);
		} else if (useEff.containsKey("goodsId")) {// 加物品
			int num = useEff.get("num") * goodsNum;
			this.addPlayerGoods(worldPlayer, useEff.get("goodsId"), num);
			HashMap<String, Integer> run = new HashMap<String, Integer>();
			run.put(useEff.get("goodsId").toString(), num);
			runData.add(run);
		} else if (useEff.containsKey("boxId")) {// 宝箱
			Map<Integer, Integer> getGoods = this.boxRandom(useEff.get("boxId"), useEff.get("num"));
			for (Entry<Integer, Integer> map : getGoods.entrySet()) {
				this.addPlayerGoods(worldPlayer, map.getKey(), map.getValue());
				HashMap<String, Integer> run = new HashMap<String, Integer>();
				run.put(map.getKey().toString(), map.getValue());
				runData.add(run);
			}
		}
		worldPlayer.addPushPlayerGoods(playerGoods);
		worldPlayer.pushUpdateGoods();
		return runData;
	}

	/**
	 * 出售物品
	 * 
	 * @param player
	 * @param id 物品流水id
	 * @param goodsNum 物品数量
	 * @return
	 * @throws PlayerDataException
	 */
	@SuppressWarnings("rawtypes")
	public int sellGoods(WorldPlayer worldPlayer, int id, int goodsNum) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		PlayerGoods playerGoods = playerGoodsDao.getPlayerGoodsById(worldPlayer.getPlayer().getId(), id);
		if (playerGoods == null) {
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		}
		int nowGoodsNum = playerGoods.getGoodsNum();
		if (nowGoodsNum < goodsNum) {
			throw new PlayerDataException(gameConfigService.getMsg(14));// 物品数量不足
		}
		Map<Integer, Map> baseGoods = gameConfigService.getGameConfig().get(Goods.class.getSimpleName());
		Map goods = baseGoods.get(playerGoods.getGoodsId());
		if (Integer.parseInt(goods.get("canSale").toString()) == 0) {
			throw new PlayerDataException(gameConfigService.getMsg(19));// 物品不能出售
		}
		// 添加金币
		int price = Integer.parseInt(goods.get("price").toString()) * goodsNum;
		ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "gold", price);
		// 修改物品数量
		if (nowGoodsNum - goodsNum > 0) {
			playerGoods.setGoodsNum(nowGoodsNum - goodsNum);
			playerGoodsDao.save(playerGoods);
		} else {
			playerGoods.setGoodsNum(0);
			playerGoodsDao.delete(playerGoods);
		}
		worldPlayer.addPushPlayerGoods(playerGoods);
		worldPlayer.pushUpdateGoods();
		return price;
	}
	/**
	 * 根据物品流水id删除背包物品
	 * 
	 * @param worldPlayer
	 * @param id 物品流水id
	 * @param goodsNum 删除数量
	 * @return
	 */
	public PlayerGoods delPlayerGoods(WorldPlayer worldPlayer, int id, int goodsNum) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		PlayerGoods playerGoods = playerGoodsDao.getPlayerGoodsById(worldPlayer.getPlayer().getId(), id);
		if (playerGoods == null || goodsNum <= 0)
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		int nowGoodsNum = playerGoods.getGoodsNum() - goodsNum;
		if (nowGoodsNum < 0)
			throw new PlayerDataException(gameConfigService.getMsg(14));// 物品数量不足
		if (nowGoodsNum > 0) {// 修改数量
			playerGoods.setGoodsNum(nowGoodsNum);
			playerGoodsDao.save(playerGoods);
		} else {
			playerGoods.setGoodsNum(0);
			playerGoodsDao.delete(playerGoods);
		}
		worldPlayer.addPushPlayerGoods(playerGoods);
		return playerGoods;
	}
	/**
	 * 删除背包物品
	 * 
	 * @param worldPlayer
	 * @param playerGoods 物品
	 * @param goodsNum 删除数量
	 * @return
	 */
	public PlayerGoods delPlayerGoods(WorldPlayer worldPlayer, PlayerGoods playerGoods, int goodsNum) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		if (playerGoods == null || goodsNum <= 0)
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		int nowGoodsNum = playerGoods.getGoodsNum() - goodsNum;
		if (nowGoodsNum < 0)
			throw new PlayerDataException(gameConfigService.getMsg(14));// 物品数量不足
		if (nowGoodsNum > 0) {// 修改数量
			playerGoods.setGoodsNum(nowGoodsNum);
			playerGoodsDao.save(playerGoods);
		} else {
			playerGoods.setGoodsNum(0);
			playerGoodsDao.delete(playerGoods);
		}
		worldPlayer.addPushPlayerGoods(playerGoods);
		return playerGoods;
	}
	/**
	 * 根据物品配置表id删除物品
	 * 
	 * @param worldPlayer
	 * @param id 物品流水id
	 * @param goodsNum 删除数量
	 * @return
	 */
	public boolean delPlayerGoodsByGoodsId(WorldPlayer worldPlayer, int goodsId, int delNum) {
		List<PlayerGoods> goodsList = playerGoodsDao.getPlayerGoodsListById(worldPlayer.getPlayer().getId(), goodsId);
		int totalNum = 0;// 物品总数量
		for (PlayerGoods playerGoods : goodsList) {
			totalNum += playerGoods.getGoodsNum();
		}
		if (totalNum < delNum)
			return false;
		for (PlayerGoods playerGoods : goodsList) {
			int goodsNum = playerGoods.getGoodsNum();
			if (goodsNum <= delNum) {// 删除物品
				playerGoods.setGoodsNum(0);
				playerGoodsDao.delete(playerGoods.getId());
				delNum -= goodsNum;
			} else {// 修改物品数量
				playerGoods.setGoodsNum(goodsNum - delNum);
				playerGoodsDao.save(playerGoods);
				break;
			}
			worldPlayer.addPushPlayerGoods(playerGoods);
		}
		return true;
	}

	/** 根据物品流水id获取物品信息 */
	public List<PlayerGoods> getGoodsInfoByIds(int playerId, Integer[] ids) {
		return playerGoodsDao.getGoodsInfoByIds(playerId, ids);
	}

	/**
	 * 获取某种物品的数量
	 * 
	 * @param worldPlayer
	 * @param goodsId 物品ID
	 * @return
	 */
	public int getPlayerGoodsNum(WorldPlayer worldPlayer, int goodsId) {
		int goodsNum = 0;
		List<PlayerGoods> goodsList = playerGoodsDao.getPlayerGoodsListById(worldPlayer.getPlayer().getId(), goodsId);
		for (PlayerGoods playerGoods : goodsList) {
			goodsNum += playerGoods.getGoodsNum();
		}
		return goodsNum;
	}

	/**
	 * 宝箱随机
	 * 
	 * @param boxId 宝箱id
	 * @param num 宝箱中出的物品数量
	 * @return Map<Integer, Integer> 物品id-》数量
	 */
	private Map<Integer, Integer> boxRandom(int boxId, int num) {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		Map<Integer, Map> boxRandomGoods = gameConfigService.getGameConfig4Key().get(Box.class.getSimpleName()).get(boxId);
		int type = 0;
		HashMap<String, Double> map = new HashMap<String, Double>();// 物品id->概率
		for (Map box : boxRandomGoods.values()) {
			String goodsId = box.get("goodsId").toString();
			Double random = Double.valueOf(box.get("random").toString());
			map.put(goodsId, random);
			type = Integer.parseInt(box.get("type").toString());
		}
		Map<Integer, Integer> getGoods = new HashMap<>();
		if (type == 1) {// 每次出一个物品，出num 个
			for (int i = 0; i < num; i++) {
				int goodsId = Integer.parseInt(CommonUtil.randDict(map));// 获得的物品id
				int goodsNum = 1;
				if (getGoods.containsKey(goodsId)) {
					goodsNum = getGoods.get(goodsId) + 1;
				}
				getGoods.put(goodsId, goodsNum);
			}
		} else if (type == 2) {// 物品单个判断是否掉落
			for (Entry<String, Double> entry : map.entrySet()) {
				int goodsId = Integer.parseInt(entry.getKey());
				double random = Double.parseDouble(entry.getValue().toString());
				if (CommonUtil.random(random))
					getGoods.put(goodsId, 1);
			}
		}
		return getGoods;
	}
	/**
	 * 添加单种物品
	 * 
	 * @param player WorldPlayer
	 * @param goodsId 物品ID
	 * @param goodsNum 物品数量
	 * @throws PlayerDataException
	 */
	@SuppressWarnings("rawtypes")
	private PlayerGoods addPlayerGoods(WorldPlayer worldPlayer, int goodsId, int goodsNum) throws PlayerDataException {
		PlayerGoods runPlayerGoods = null;
		if (goodsNum == 0)
			return runPlayerGoods;
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		Map<Integer, Map> baseGoods = gameConfigService.getGameConfig().get(Goods.class.getSimpleName());
		Map goods = baseGoods.get(goodsId);
		if (goods == null) {
			Map<Integer, Map> lan = gameConfigService.getGameConfig().get(BaseLanguage.class.getSimpleName());
			throw new PlayerDataException(lan.get(20).get("msg").toString());// 物品不存在
		}
		int maxNum = Integer.parseInt(goods.get("overLap").toString()); // 堆叠数量
		// 根据id查询数据库里面是否存在此物品
		PlayerGoods playerGoods = playerGoodsDao.getGoodsInfoById(worldPlayer.getPlayer().getId(), goodsId, maxNum);
		if (playerGoods != null) {// 修改物品数量
			runPlayerGoods = playerGoods;
			int oldGoodsNum = playerGoods.getGoodsNum();
			if (oldGoodsNum + goodsNum >= maxNum) {
				playerGoods.setGoodsNum(maxNum);
				goodsNum = oldGoodsNum + goodsNum - maxNum;// 剩余要添加的个数
				if (goodsNum > 0)
					runPlayerGoods = this.insertGoodsToDb(worldPlayer, goods, goodsNum);
			} else {
				playerGoods.setGoodsNum(oldGoodsNum + goodsNum);
			}
			playerGoods.setDateTime(new Date());
			this.playerGoodsDao.save(playerGoods);
			worldPlayer.addPushPlayerGoods(playerGoods);
			return runPlayerGoods;
		} else {
			return this.insertGoodsToDb(worldPlayer, goods, goodsNum);
		}
	}

	/**
	 * 插入物品
	 * 
	 * @param player
	 * @param goods 物品信息
	 * @param goodsNum 物品数量
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private PlayerGoods insertGoodsToDb(WorldPlayer worldPlayer, Map goods, int goodsNum) {
		int goodsId = Integer.parseInt(goods.get("id").toString()); // 物品id
		int maxNum = Integer.parseInt(goods.get("overLap").toString()); // 堆叠数量
		if (maxNum == 0)
			maxNum = 1;
		int num = (int) Math.ceil((double) goodsNum / maxNum);// 进一取整(格子数
		Player player = worldPlayer.getPlayer();
		PlayerGoods playerGoods = null;
		for (int i = 0; i < num; i++) {
			PlayerGoods newGoods = new PlayerGoods();
			newGoods.setPlayerId(player.getId());
			newGoods.setGoodsId(goodsId);
			newGoods.setGoodsName(goods.get("name").toString());
			newGoods.setGoodsQuality(Integer.parseInt(goods.get("quality").toString()));
			newGoods.setGoodsStar(0);
			newGoods.setGoodsType(Integer.parseInt(goods.get("type").toString()));
			newGoods.setSubType(Integer.parseInt(goods.get("subType").toString()));
			newGoods.setDateTime(new Date());
			HashMap baseProperty = new HashMap();
			baseProperty.put("aa", Arrays.asList(goods.get("aa").toString()));
			baseProperty.put("ab", Arrays.asList(goods.get("ab").toString()));
			baseProperty.put("ac", Arrays.asList(goods.get("ac").toString()));
			baseProperty.put("ad", Arrays.asList(goods.get("ad").toString()));
			baseProperty.put("ae", Arrays.asList(goods.get("ae").toString()));
			baseProperty.put("af", Arrays.asList(goods.get("af").toString()));
			baseProperty.put("ag", Arrays.asList(goods.get("ag").toString()));
			baseProperty.put("ah", Arrays.asList(goods.get("ah").toString()));
			baseProperty.put("ai", Arrays.asList(goods.get("ai").toString()));
			baseProperty.put("aj", Arrays.asList(goods.get("aj").toString()));
			baseProperty.put("ak", Arrays.asList(goods.get("ak").toString()));
			baseProperty.put("al", Arrays.asList(goods.get("al").toString()));
			baseProperty.put("am", Arrays.asList(goods.get("am").toString()));
			baseProperty.put("an", Arrays.asList(goods.get("an").toString()));
			baseProperty.put("ao", Arrays.asList(goods.get("ao").toString()));
			baseProperty.put("ap", Arrays.asList(goods.get("ap").toString()));
			baseProperty.put("aq", Arrays.asList(goods.get("aq").toString()));
			baseProperty.put("ar", Arrays.asList(goods.get("ar").toString()));
			newGoods.setProperty(CommonUtil.mapToStr(baseProperty));
			int setNum = 0;
			if (i == num - 1) // 最后一个
				setNum = goodsNum;
			else
				setNum = maxNum;
			goodsNum -= setNum;
			newGoods.setGoodsNum(setNum);
			playerGoods = playerGoodsDao.insert(newGoods);
			worldPlayer.addPushPlayerGoods(playerGoods);
		}
		return playerGoods;
	}

	/**
	 * 判断背包物品是否达到最大
	 * 
	 * @param player
	 * @param goodsMap
	 * @return true 已经满了
	 */
	private boolean isPackFull(WorldPlayer player) {
		int nowNum = playerGoodsDao.getCount(player.getPlayer().getId());
		return nowNum >= MAX_SLOT_NUM;
	}
	/**
	 * GM 获取游戏内装备信息
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map getGoodsListForGm() {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		Map<Integer, Map> goods = gameConfigService.getGameConfig().get(Goods.class.getSimpleName());
		Map<String, Map<String, String>> goodsData = new HashMap<String, Map<String, String>>();
		Map<String, String> goodsTypeData = new HashMap<String, String>();
		Map<String, String> typeName = new HashMap<String, String>();// 类型名称
		typeName.put("resources", "资源");// 如金币等
		typeName.put("0_0", "武器");
		typeName.put("0_1", "胸甲");
		typeName.put("0_2", "护手");
		typeName.put("0_3", "鞋子");
		typeName.put("0_4", "项链");
		typeName.put("0_5", "戒指");
		typeName.put("1_0", "兵种碎片");
		typeName.put("1_1", "神兽碎片");
		typeName.put("2_0", "货币");
		typeName.put("2_1", "经验卡");
		typeName.put("2_2", "宝箱");
		typeName.put("2_3", "科宝材料");
		typeName.put("2_4", "兵种材料");
		typeName.put("2_5", "神器材料");
		typeName.put("2_6", "装备材料");
		typeName.put("2_7", "天赋技能点");

		for (Entry<Integer, Map> goodsEntry : goods.entrySet()) {
			String id = goodsEntry.getValue().get("id").toString();
			String goodsType = goodsEntry.getValue().get("type").toString();
			String goodsSubType = goodsEntry.getValue().get("subType").toString();
			String type = goodsType + "_" + goodsSubType;
			String name = goodsEntry.getValue().get("name").toString();

			if (goodsData.containsKey(type)) {
				Map<String, String> goodsData1 = goodsData.get(type);
				goodsData1.put(id, name);
			} else {
				Map<String, String> goodsData1 = new HashMap<String, String>();
				goodsData1.put(id, name);
				goodsData.put(type, goodsData1);
			}
			if (!goodsTypeData.containsKey(type)) {
				goodsTypeData.put(type, typeName.get(type));
			}
		}
		Map<String, String> goodsData1 = new HashMap<String, String>();
		goodsData1.put("gold", "金币");
		goodsData1.put("diamond", "钻石");
		goodsData1.put("power", "粮草");
		goodsData.put("resources", goodsData1);
		goodsData.put("type", goodsTypeData);
		goodsTypeData.put("resources", typeName.get("resources"));

		Map<String, Map> runData = new HashMap<String, Map>();
		runData.put("data", goodsData);
		return runData;
	}

}
