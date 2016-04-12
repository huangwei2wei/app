package com.app.empire.world.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.app.empire.protocol.data.hero.UpdateHeroData;
import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.dao.mongo.impl.PlayerHeroDao;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mongo.PlayerGoods;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.entity.mongo.PlayerHeroEquip;
import com.app.empire.world.entity.mysql.gameConfig.BaseLanguage;
import com.app.empire.world.entity.mysql.gameConfig.Goods;
import com.app.empire.world.entity.mysql.gameConfig.HeroExt;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;

/**
 * HeroService 处理与英雄相关操作业务处理逻辑层
 * 
 * @since JDK 1.6
 */

@Service
public class PlayerHeroService {
	private Logger log = Logger.getLogger(PlayerHeroService.class);
	@Autowired
	private PlayerHeroDao heroDao;

	/**
	 * 获得一个新英雄
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public PlayerHero addHero(WorldPlayer worldPlayer, int heroExtId) throws PlayerDataException {
		Player player = worldPlayer.getPlayer();
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		Map<Integer, Map> baseHeroExt = gameConfigService.getGameConfig().get(HeroExt.class.getSimpleName());
		Map heroExt = baseHeroExt.get(heroExtId);
		if (heroExt == null) {
			log.debug(heroExtId + "号英雄不存在");
			throw new PlayerDataException(gameConfigService.getMsg(10));// 英雄不存在
		}
		HashMap baseProperty = new HashMap();
		baseProperty.put("aa", Arrays.asList(heroExt.get("aa").toString()));
		baseProperty.put("ab", Arrays.asList(heroExt.get("ab").toString()));
		baseProperty.put("ac", Arrays.asList(heroExt.get("ac").toString()));
		baseProperty.put("ad", Arrays.asList(heroExt.get("ad").toString()));
		baseProperty.put("ae", Arrays.asList(heroExt.get("ae").toString()));
		baseProperty.put("af", Arrays.asList(heroExt.get("af").toString()));
		baseProperty.put("ag", Arrays.asList(heroExt.get("ag").toString()));
		baseProperty.put("ah", Arrays.asList(heroExt.get("ah").toString()));
		baseProperty.put("ai", Arrays.asList(heroExt.get("ai").toString()));
		baseProperty.put("aj", Arrays.asList(heroExt.get("aj").toString()));
		baseProperty.put("ak", Arrays.asList(heroExt.get("ak").toString()));
		baseProperty.put("al", Arrays.asList(heroExt.get("al").toString()));
		baseProperty.put("am", Arrays.asList(heroExt.get("am").toString()));
		baseProperty.put("an", Arrays.asList(heroExt.get("an").toString()));
		baseProperty.put("ao", Arrays.asList(heroExt.get("ao").toString()));
		baseProperty.put("ap", Arrays.asList(heroExt.get("ap").toString()));
		baseProperty.put("aq", Arrays.asList(heroExt.get("aq").toString()));
		baseProperty.put("ar", Arrays.asList(heroExt.get("ar").toString()));

		PlayerHero newHero = new PlayerHero();
		newHero.setPlayerId(player.getId());
		newHero.setHeroBaseId(Integer.parseInt(heroExt.get("heroBaseId").toString()));
		newHero.setHeroExtId(heroExtId);
		newHero.setProperty(CommonUtil.mapToStr(baseProperty));
		newHero.setLv(Integer.parseInt(heroExt.get("lv").toString()));
		newHero.setTalent(50);// 测试送50 天赋点
		return this.heroDao.insert(worldPlayer, newHero);

	}
	/**
	 * 英雄吃经验、天赋等
	 * 
	 * @param worldPlayer
	 * @param heroId 英雄流水id
	 * @param goodsId 物品流水id
	 * @param num 使用物品数量
	 */
	public void useGoods(WorldPlayer worldPlayer, int heroId, int goodsId, int num) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		int playerId = worldPlayer.getPlayer().getId();
		PlayerHero playerHero = this.getHeroByHeroId(worldPlayer, heroId);
		if (playerHero == null)
			throw new PlayerDataException(gameConfigService.getMsg(10));// 英雄不存在

		PlayerGoods playerGoods = ServiceManager.getManager().getPlayerGoodsService().getPlayerGoodsById(playerId, goodsId);
		if (playerGoods == null || num <= 0)
			throw new PlayerDataException(gameConfigService.getMsg(20));// 物品不存在
		int nowGoodsNum = playerGoods.getGoodsNum() - num;
		if (nowGoodsNum < 0)
			throw new PlayerDataException(gameConfigService.getMsg(14));// 物品数量不足

		Map<Integer, Map> baseGoods = gameConfigService.getGameConfig().get(Goods.class.getSimpleName());
		Map goods = baseGoods.get(playerGoods.getGoodsId());
		// 获取使用效果
		String useEffect = goods.get("usingEffect").toString();
		if (useEffect == null || useEffect.toString().equals(""))
			throw new PlayerDataException(gameConfigService.getMsg(15));// 物品不能使用
		Map<String, Integer> useEffectList = (Map<String, Integer>) JSON.parse(useEffect);
		if (useEffectList.containsKey("heroExp")) {
			int exp = useEffectList.get("heroExp");
			this.addHeroEXP(worldPlayer, playerHero, exp);
		} else if (useEffectList.containsKey("talent")) {
			int talent = useEffectList.get("talent");
			this.useTalent(worldPlayer, playerHero, talent);
		}
		ServiceManager.getManager().getPlayerGoodsService().delPlayerGoods(worldPlayer, playerGoods, num);
		worldPlayer.pushUpdateGoods();
	}
	/**
	 * 根据玩家id获取英雄列表
	 * 
	 * @param playerId
	 * @return
	 */
	public List<PlayerHero> getHeroListByPlayerId(WorldPlayer worldPlayer) {
		return heroDao.getHeroListByPlayerId(worldPlayer);
	}
	/**
	 * 根据玩家id和英雄id获取英雄列表
	 * 
	 * @param playerId
	 * @return
	 */
	public List<PlayerHero> getHeroListByPlayerIdAndHeroId(WorldPlayer worldPlayer, List<Integer> heroId) {
		return heroDao.getHeroListByPlayerIdAndHeroId(worldPlayer, heroId);
	}
	/**
	 * 根据英雄id获取英雄
	 * 
	 * @param playerId
	 * @return
	 */
	public PlayerHero getHeroByHeroId(WorldPlayer worldPlayer, Integer heroId) {
		return heroDao.getHeroByHeroId(worldPlayer, heroId);
	}

	/***
	 * 英雄加经验
	 * 
	 * @param playerHero
	 * @param exp
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void addHeroEXP(WorldPlayer worldPlayer, PlayerHero playerHero, int exp) throws PlayerDataException {
		int playerLv = worldPlayer.getPlayer().getLv();// 角色等级
		int lv = playerHero.getLv();
		int lvExp = playerHero.getExperience() + exp;
		int maxLv = ServiceManager.getManager().getConfiguration().getInt("maxlevel");
		if (lv < maxLv) {// 等级上线
			Map<Integer, Map> heroExt = ServiceManager.getManager().getGameConfigService().getGameConfig().get(HeroExt.class.getSimpleName());
			Integer needExperience = (Integer) heroExt.get(playerHero.getHeroExtId()).get("experience");
			while (lvExp >= needExperience) {// 升级
				Integer nextHeroExtId = (Integer) heroExt.get(playerHero.getHeroExtId()).get("nextHeroExtId");
				needExperience = (Integer) heroExt.get(nextHeroExtId).get("experience");
				Integer newLv = (Integer) heroExt.get(nextHeroExtId).get("lv");
				if (newLv > playerLv)
					break;
				playerHero.setHeroExtId(nextHeroExtId);// 设置扩展id
				playerHero.setLv(newLv);
				playerHero.setTalent(playerHero.getTalent() + 1);// 添加英雄天赋值
				lvExp = lvExp - needExperience;
			}
		}
		playerHero.setExperience(lvExp);
		heroDao.save(playerHero);
		/** 推送 */
		Map<String, String> info = new HashMap<String, String>();
		info.put("lv", lv + "");
		info.put("lvExp", lvExp + "");
		info.put("talent", playerHero.getTalent() + "");
		sendUpdateHero(worldPlayer, info, playerHero);
	}
	/* 推送英雄修改的数据　如经验等级等 */
	public void sendUpdateHero(WorldPlayer worldPlayer, Map<String, String> info, PlayerHero hero) {
		if (info == null || info.size() == 0)
			return;
		UpdateHeroData updateHeroData = new UpdateHeroData();
		updateHeroData.setHeroId(hero.getId());
		updateHeroData.setKey((String[]) info.keySet().toArray(new String[info.size()]));
		updateHeroData.setValue((String[]) info.values().toArray(new String[info.size()]));
		worldPlayer.sendData(updateHeroData);
	}
	/**
	 * 玩家英雄消耗/获得天赋（无保存数据
	 * 
	 * @param worldPlayer
	 * @param playerHero
	 * @param talent 可正负
	 * @return
	 * @throws PlayerDataException
	 */
	@SuppressWarnings("rawtypes")
	public PlayerHero useTalent(WorldPlayer worldPlayer, PlayerHero playerHero, int talent) throws PlayerDataException {
		Map<String, String> info = new HashMap<String, String>();
		int myTalent = playerHero.getTalent();
		int nowTalent = myTalent + talent;
		if (nowTalent < 0) {
			GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
			Map<Integer, Map> lan = gameConfigService.getGameConfig().get(BaseLanguage.class.getSimpleName());
			throw new PlayerDataException(lan.get(9).get("msg").toString());// 天赋值数量不足
		}
		if (talent < 0) {
			int useTalent = playerHero.getUseTalent() + Math.abs(talent);
			playerHero.setUseTalent(useTalent);
			info.put("useTalent", useTalent + "");
		}
		playerHero.setTalent(nowTalent);
		// heroDao.save(playerHero);
		/** 推送 */
		info.put("talent", nowTalent + "");
		sendUpdateHero(worldPlayer, info, playerHero);
		return playerHero;
	}

	/**
	 * 计算玩家英雄属性
	 * 
	 * @param worldPlayer
	 * @param playerHero
	 * @return
	 */
	public PlayerHero statisticsProperty(WorldPlayer worldPlayer, PlayerHero playerHero) {

		return null;
	}

}
