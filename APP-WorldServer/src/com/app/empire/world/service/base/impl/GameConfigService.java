package com.app.empire.world.service.base.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.app.db.mysql.dao.impl.UniversalDaoHibernate;
import com.app.empire.world.dao.mysql.gameConfig.impl.BaseLanguageDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.BaseModuleDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.BaseModuleSubDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.BaseRandomNameDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.BoxDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.EquipAchieveDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.EquipBarDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.EquipRefineDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.GoodsDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.HeroDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.HeroExtDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.MapCopyDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.ParameterDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.PlayerLvDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.ShopDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.SkillDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.SkillExtDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.TaskDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.TradeNpcDao;
import com.app.empire.world.dao.mysql.gameConfig.impl.TradeVipDao;
import com.app.empire.world.entity.mysql.gameConfig.BaseLanguage;
import com.app.empire.world.entity.mysql.gameConfig.BaseModule;
import com.app.empire.world.entity.mysql.gameConfig.BaseModuleSub;
import com.app.empire.world.entity.mysql.gameConfig.BaseRandomName;
import com.app.empire.world.entity.mysql.gameConfig.Box;
import com.app.empire.world.entity.mysql.gameConfig.EquipAchieve;
import com.app.empire.world.entity.mysql.gameConfig.EquipBar;
import com.app.empire.world.entity.mysql.gameConfig.EquipRefine;
import com.app.empire.world.entity.mysql.gameConfig.Goods;
import com.app.empire.world.entity.mysql.gameConfig.Hero;
import com.app.empire.world.entity.mysql.gameConfig.HeroExt;
import com.app.empire.world.entity.mysql.gameConfig.MapCopy;
import com.app.empire.world.entity.mysql.gameConfig.Parameter;
import com.app.empire.world.entity.mysql.gameConfig.PlayerLv;
import com.app.empire.world.entity.mysql.gameConfig.Skill;
import com.app.empire.world.entity.mysql.gameConfig.SkillExt;
import com.app.empire.world.entity.mysql.gameConfig.Store;
import com.app.empire.world.entity.mysql.gameConfig.Task;
import com.app.empire.world.entity.mysql.gameConfig.TradeNpc;
import com.app.empire.world.entity.mysql.gameConfig.TradeVip;

/**
 * 加载游戏配置
 */

@SuppressWarnings(value = {"rawtypes", "unchecked"})
@Service
public class GameConfigService {
	@Autowired
	private BaseLanguageDao baseLanguageDao;// 语言提示包配置
	@Autowired
	private HeroDao heroDao;// 英雄基表配置
	@Autowired
	private HeroExtDao heroExtDao;// 英雄扩展表配置
	@Autowired
	private BaseRandomNameDao baseRandomNameDao;// 随机昵称配置
	@Autowired
	private PlayerLvDao playerLvDao;// 角色升级经验配置
	@Autowired
	private SkillDao skillDao;// 技能基表
	@Autowired
	private SkillExtDao skillExtDao;// 技能扩展表
	@Autowired
	TaskDao taskDao;// 任务
	@Autowired
	private GoodsDao goodsDao;// 物品表
	@Autowired
	private EquipBarDao equipBarDao;// 装备栏信息表
	@Autowired
	private EquipAchieveDao equipAchieveDao;// 装备成就表
	@Autowired
	private EquipRefineDao equipRefineDao;// 装备精炼表
	@Autowired
	BaseModuleDao baseModuleDao;// 模块基表
	@Autowired
	BaseModuleSubDao baseModuleSubDao;// 模块扩展表
	@Autowired
	MapCopyDao mapCopyDao;// 副本地图
	@Autowired
	BoxDao boxDao; // 宝箱
	@Autowired
	ShopDao shopDao;// 玩家商店
	@Autowired
	ParameterDao parameterDao;// 参数表
	@Autowired
	TradeNpcDao tradeNpcDao;// 兑换npc
	@Autowired
	TradeVipDao tradeVipDao;// vip

	/** 一般格式配置　表名->id->map */
	private HashMap<String, Map<Integer, Map>> gameConfig = new HashMap<String, Map<Integer, Map>>();
	/** 指定key格式配置　表名->groupKey->id->map */
	private HashMap<String, Map<Integer, Map<Integer, Map>>> gameConfig4Key = new HashMap<String, Map<Integer, Map<Integer, Map>>>();
	/** 多键值分组格式配置　表名->String->map */
	private HashMap<String, Map<String, Map>> gameConfig4MulKey = new HashMap<String, Map<String, Map>>();
	/**
	 * 一般格式配置　id->map
	 * 
	 * @param dao
	 * @param clazz
	 */
	private void loadConfig(UniversalDaoHibernate dao, Class clazz, String key) {
		List<?> rsl = dao.getAll(clazz);
		String tableKey = clazz.getSimpleName();
		this.gameConfig.remove(tableKey);
		Map<Integer, Map> map2 = new HashMap<Integer, Map>();
		for (Object object : rsl) {
			Map map1 = (Map) JSON.toJSON(object);
			map2.put(Integer.parseInt(map1.get(key).toString()), map1);
		}
		this.gameConfig.put(tableKey, map2);
	}

	/**
	 * 多键值 一般格式配置　key->map
	 * 
	 * @param dao
	 * @param clazz
	 * @param key1 键名1
	 * @param key2 键名2
	 */
	private void loadConfig(UniversalDaoHibernate dao, Class clazz, String key1, String key2) {
		List<?> rsl = dao.getAll(clazz);
		String classSimpleName = clazz.getSimpleName();
		this.gameConfig.remove(classSimpleName);
		Map map2 = new HashMap();
		for (Object object : rsl) {
			Map map1 = (Map) JSON.toJSON(object);
			String key = map1.get(key1) + "_" + map1.get(key2);
			map2.put(key, map1);
		}
		this.gameConfig4MulKey.put(classSimpleName, map2);
	}

	/**
	 * 指定key格式配置　表名->groupKey->id->map
	 * 
	 * @param dao
	 * @param clazz
	 * @param groupKey 要分组的键
	 */
	private void loadConfig4Key(UniversalDaoHibernate dao, Class clazz, String groupKey) {
		List<?> rsl = dao.getAll(clazz);
		String classSimpleName = clazz.getSimpleName();
		this.gameConfig4Key.remove(classSimpleName);

		HashMap<Integer, Map<Integer, Map>> runMap = new HashMap<Integer, Map<Integer, Map>>();
		for (Object object : rsl) {
			Map dataMap = (Map) JSON.toJSON(object);
			Integer keyValue = (Integer) dataMap.get(groupKey);
			if (runMap.containsKey(keyValue)) {
				Map groupMap = runMap.get(keyValue);
				groupMap.put(dataMap.get("id"), dataMap);
			} else {
				Map<Integer, Map> m = new HashMap<Integer, Map>();
				m.put((Integer) dataMap.get("id"), dataMap);
				runMap.put(keyValue, m);
			}
		}
		this.gameConfig4Key.put(classSimpleName, runMap);
	}

	public void load() {
		this.loadConfig(parameterDao, Parameter.class, "id");
		this.loadConfig(baseLanguageDao, BaseLanguage.class, "id");
		this.loadConfig(heroDao, Hero.class, "id");
		this.loadConfig(heroExtDao, HeroExt.class, "id");
		this.loadConfig(baseRandomNameDao, BaseRandomName.class, "id");
		this.loadConfig(playerLvDao, PlayerLv.class, "id");
		this.loadConfig(goodsDao, Goods.class, "id");
		this.loadConfig(equipBarDao, EquipBar.class, "heroType", "stage");
		this.loadConfig(equipRefineDao, EquipRefine.class, "quality", "star");
		this.loadConfig(equipAchieveDao, EquipAchieve.class, "id");
		this.loadConfig(skillDao, Skill.class, "id");// 技能基表
		this.loadConfig(skillExtDao, SkillExt.class, "id");// 技能扩展
		this.loadConfig(taskDao, Task.class, "id");// 任务
		this.loadConfig(baseModuleDao, BaseModule.class, "id");// 模块
		this.loadConfig(baseModuleSubDao, BaseModuleSub.class, "id");// 模块
		this.loadConfig(mapCopyDao, MapCopy.class, "id");// 副本
		this.loadConfig4Key(boxDao, Box.class, "boxId");// 宝箱
		this.loadConfig4Key(shopDao, Store.class, "grade");// 商店
		this.loadConfig(shopDao, Store.class, "id");// 商店
		this.loadConfig(tradeNpcDao, TradeNpc.class, "type", "npcLv");// 兑换npc
		this.loadConfig(tradeVipDao, TradeVip.class, "vipLv");// vip

		 System.out.println(this.gameConfig4MulKey);

	}

	public HashMap<String, Map<Integer, Map>> getGameConfig() {
		return this.gameConfig;
	}
	public HashMap<String, Map<Integer, Map<Integer, Map>>> getGameConfig4Key() {
		return gameConfig4Key;
	}
	public HashMap<String, Map<String, Map>> getGameConfig4MulKey() {
		return gameConfig4MulKey;
	}

	/**
	 * 根据id,获取消息
	 */
	public String getMsg(int id) {
		Map language = this.gameConfig.get(BaseLanguage.class.getSimpleName()).get(id);
		if (language == null)
			return "";
		return language.get("msg").toString();
	}

	/**
	 * 根据id,获取参数信息
	 */
	public String getPar(int id) {
		Map parameter = this.gameConfig.get(Parameter.class.getSimpleName()).get(id);
		if (parameter == null)
			return "";
		return parameter.get("parameter").toString();
	}

}
