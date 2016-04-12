package com.app.empire.world.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.dao.mongo.impl.PlayerEquipDao;
import com.app.empire.world.dao.mongo.impl.PlayerHeroDao;
import com.app.empire.world.entity.mongo.HeroEquipGoods;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.entity.mongo.PlayerHeroEquip;
import com.app.empire.world.entity.mongo.Skill;
import com.app.empire.world.entity.mysql.gameConfig.HeroExt;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.thread.HandlerThreadPool;

/***
 * 计算玩家属性及战力
 * 
 * @author doter
 * 
 */

@Service
public class ForceCalculateService {
	private Logger log = Logger.getLogger(ForceCalculateService.class);
	private HandlerThreadPool threadPool = new HandlerThreadPool(2, 6, 10);
	// private ExecutorService threadPool = Executors.newCachedThreadPool();
	@Autowired
	private PlayerHeroDao heroDao; // 英雄数据操作类
	@Autowired
	private PlayerEquipDao equipDao;// 英雄装备操作类
	/**
	 * 计算玩家英雄属性
	 * 
	 * @param worldPlayer
	 * @param playerHero //修改的英雄
	 * @return
	 */
	public void statisticsProperty(WorldPlayer worldPlayer, PlayerHero playerHero) {
		try {
			threadPool.execute(new ThreadTask(worldPlayer, playerHero));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ThreadTask implements Runnable {
		private WorldPlayer worldPlayer;
		private PlayerHero playerHero;
		private HashMap<String, Double> proMap = new HashMap<String, Double>(); // 所有属性集
		/**
		 * @param worldPlayer
		 * @param playerHero 修改的英雄
		 */
		public ThreadTask(WorldPlayer worldPlayer, PlayerHero playerHero) {
			this.worldPlayer = worldPlayer;
			this.playerHero = playerHero;
		}

		@Override
		public void run() {
			try {
				this.statisticsHeroProperty(worldPlayer, playerHero);
			} catch (Throwable e) {
				Log.info(e.getMessage());
				e.printStackTrace();
			}
		}

		/**
		 * 计算玩家英雄属性集合及战力
		 * 
		 * @param worldPlayer
		 * @param playerHero 修改的英雄
		 * @return
		 */
		public void statisticsHeroProperty(WorldPlayer worldPlayer, PlayerHero playerHero) {
			int heroExtId = playerHero.getHeroExtId();
			GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
			Map<Integer, Map> HeroExtMap = gameConfigService.getGameConfig().get(HeroExt.class.getSimpleName());
			Map heroExt = HeroExtMap.get(heroExtId);
			// 基本
			proMap.put("aa", Double.parseDouble(heroExt.get("aa").toString()));
			proMap.put("ab", Double.parseDouble(heroExt.get("ab").toString()));
			proMap.put("ac", Double.parseDouble(heroExt.get("ac").toString()));
			proMap.put("ad", Double.parseDouble(heroExt.get("ad").toString()));
			proMap.put("ae", Double.parseDouble(heroExt.get("ae").toString()));
			proMap.put("af", Double.parseDouble(heroExt.get("af").toString()));
			proMap.put("ag", Double.parseDouble(heroExt.get("ag").toString()));
			proMap.put("ah", Double.parseDouble(heroExt.get("ah").toString()));
			proMap.put("ai", Double.parseDouble(heroExt.get("ai").toString()));
			proMap.put("aj", Double.parseDouble(heroExt.get("aj").toString()));
			proMap.put("ak", Double.parseDouble(heroExt.get("ak").toString()));
			proMap.put("al", Double.parseDouble(heroExt.get("al").toString()));
			proMap.put("am", Double.parseDouble(heroExt.get("am").toString()));
			proMap.put("an", Double.parseDouble(heroExt.get("an").toString()));
			proMap.put("ao", Double.parseDouble(heroExt.get("ao").toString()));
			proMap.put("ap", Double.parseDouble(heroExt.get("ap").toString()));
			proMap.put("aq", Double.parseDouble(heroExt.get("aq").toString()));
			proMap.put("ar", Double.parseDouble(heroExt.get("ar").toString()));
			// 技能属性
			Map<Integer, Skill> skills = playerHero.getSkill();
			for (Skill skill : skills.values()) {
				Map<String, List<Object>> skillPro = CommonUtil.strToMap(skill.getProperty());
				for (Entry<String, List<Object>> proEntry : skillPro.entrySet()) {
					String key = proEntry.getKey();
					double val = Double.parseDouble(proEntry.getValue().get(0).toString());
					if (proMap.containsKey(key))
						val += this.proMap.get(key);
					proMap.put(key, val);
				}
			}
			// 装备属性
			List<PlayerHeroEquip> playerHeroEquipList = equipDao.getHeroEquip(worldPlayer, playerHero.getId());// 英雄所有军阶的装备
			for (PlayerHeroEquip playerHeroEquip : playerHeroEquipList) {
				Map<Integer, HeroEquipGoods> equipGoods = playerHeroEquip.getEquip();
				int achieveProAdd = playerHeroEquip.getAchieveProAdd();// 激活属性加成
				for (HeroEquipGoods heroEquip : equipGoods.values()) {
					Map<String, List<Object>> heroEquipPro = CommonUtil.strToMap(heroEquip.getProperty());// 装备属性
					int proAdd = heroEquip.getProAdd();// 精炼属性加成
					for (Entry<String, List<Object>> entry : heroEquipPro.entrySet()) {
						String key = entry.getKey();
						double val = Double.parseDouble(entry.getValue().get(0).toString()) * ((100 + achieveProAdd + proAdd) / 100);
						if (proMap.containsKey(key))
							val += this.proMap.get(key);
						val = (double) Math.round(val * 100) / 100;
						proMap.put(key, val);
					}
				}
			}
			playerHero.setProperty(CommonUtil.mapToStr2(proMap));
			heroDao.save(playerHero);
			System.out.println("英雄id " + playerHero.getId() + " pro: " + CommonUtil.mapToStr2(proMap));
			// 计算战力

		}

	}

}
