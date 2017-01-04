package com.app.empire.world.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.dao.mongo.impl.PlayerEquipDao;
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
	private PlayerEquipDao equipDao;// 英雄装备操作类

	/**
	 * 计算玩家英雄属性
	 * 
	 * @param worldPlayer
	 * @param playerHero //修改的英雄
	 * @return
	 */
	public void statisticsProperty(WorldPlayer worldPlayer) {
		try {
			threadPool.execute(new ThreadTask(worldPlayer));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ThreadTask implements Runnable {
		private WorldPlayer worldPlayer;
		private HashMap<String, Double> proMap = new HashMap<String, Double>(); // 所有属性集

		/**
		 * @param worldPlayer
		 * @param playerHero 修改的英雄
		 */
		public ThreadTask(WorldPlayer worldPlayer) {
			this.worldPlayer = worldPlayer;
		}

		@Override
		public void run() {
			try {
				this.statisticsHeroProperty(worldPlayer);
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
		public void statisticsHeroProperty(WorldPlayer worldPlayer) {
			GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
			Map<Integer, Map> HeroExtMap = gameConfigService.getGameConfig().get(HeroExt.class.getSimpleName());

			// 基本属性

			// 技能属性

			// 装备属性

			// 计算战力

		}

	}

}
