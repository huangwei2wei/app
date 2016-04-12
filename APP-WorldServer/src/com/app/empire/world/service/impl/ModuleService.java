package com.app.empire.world.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mysql.gameConfig.BaseModuleSub;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;

/**
 * 模块
 */
@Service
public class ModuleService {

	/**
	 * 判断模块是否开启
	 * 
	 * @param worldPlayer
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean isOpen(WorldPlayer worldPlayer, int subModuleId) {
		int lv = worldPlayer.getPlayer().getLv();
		int vipLv = worldPlayer.getPlayer().getVipLv();
		HashMap<String, Map<Integer, Map>> gameConfig = ServiceManager.getManager().getGameConfigService().getGameConfig();
		Map<Integer, Map> baseModuleSub = gameConfig.get(BaseModuleSub.class.getSimpleName());
		String openCondition = baseModuleSub.get(subModuleId).get("openCondition").toString();
		Map m = (Map) JSON.parse(openCondition);
		if (m == null)
			return true;
		if (m.containsKey("vip")) {
			int conditionVip = Integer.parseInt(m.get("vip").toString());
			if (vipLv >= conditionVip)
				return true;
		}
		if (m.containsKey("lv")) {
			int conditionLv = Integer.parseInt(m.get("lv").toString());
			if (lv >= conditionLv)
				return true;
		}
		return false;
	}

	/**
	 * 使用模块
	 * 
	 * @param worldPlayer
	 * @param subModuleId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean useModule(WorldPlayer worldPlayer, int subModuleId) {
		Player player = worldPlayer.getPlayer();
		int lv = player.getLv();
		int vipLv = player.getVipLv();
		HashMap<String, Map<Integer, Map>> gameConfig = ServiceManager.getManager().getGameConfigService().getGameConfig();
		Map<Integer, Map> baseModuleSub = gameConfig.get(BaseModuleSub.class.getSimpleName());
		String useCondition = baseModuleSub.get(subModuleId).get("useCondition").toString();
		if (useCondition == null || useCondition.equals(""))
			return true;
		Map<String, Map<Integer, Integer>> useConditionMap = (Map) JSON.parse(useCondition);
		if (useConditionMap == null || useConditionMap.isEmpty())
			return true;
		Integer canUseCount = 0;
		if (useConditionMap.containsKey("vip")) {
			canUseCount = useConditionMap.get("vip").get(vipLv);
		}
		if (useConditionMap.containsKey("lv")) {
			Integer canUseCount2 = useConditionMap.get("lv").get(lv);
			if (canUseCount2 > canUseCount)
				canUseCount = canUseCount2;
		}
		String useModuleCount = player.getModuleUseInfo();// 模块已经使用的次数
		Map<String, List<Object>> useModuleMap = CommonUtil.strToMap(useModuleCount);
		List<Object> arr = useModuleMap.get(subModuleId + "");
		int alreadyUseCount = 0;
		if (arr != null) {
			alreadyUseCount = (Integer) arr.get(0);
		}
		if (alreadyUseCount >= canUseCount) {
			return false;
		}
		arr.add(0, alreadyUseCount + 1);
		useModuleCount = CommonUtil.mapToStr(useModuleMap);
		player.setModuleUseInfo(useModuleCount);
		return true;
	}

	/**
	 * 购买模块
	 * 
	 * @param worldPlayer
	 * @param subModuleId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean buyModule(WorldPlayer worldPlayer, int subModuleId) {
		Player player = worldPlayer.getPlayer();
		int lv = player.getLv();
		int vipLv = player.getVipLv();
		HashMap<String, Map<Integer, Map>> gameConfig = ServiceManager.getManager().getGameConfigService().getGameConfig();
		Map<Integer, Map> baseModuleSub = gameConfig.get(BaseModuleSub.class.getSimpleName());
		String buyCondition = baseModuleSub.get(subModuleId).get("buyCondition").toString();
		if (buyCondition == null || buyCondition.equals(""))
			return true;
		Map<String, Map<Integer, Integer>> buyConditionMap = (Map) JSON.parse(buyCondition);
		if (buyConditionMap == null || buyConditionMap.isEmpty())
			return true;
		Integer canBuyCount = 0;
		if (buyConditionMap.containsKey("vip")) {
			canBuyCount = buyConditionMap.get("vip").get(vipLv);
		}
		if (buyConditionMap.containsKey("lv")) {
			Integer canBuyCount2 = buyConditionMap.get("lv").get(lv);
			if (canBuyCount2 > canBuyCount)
				canBuyCount = canBuyCount2;
		}
		String buyModuleCount = player.getModuleBuyInfo();// 模块已经使用的次数
		Map<String, List<Object>> buyModuleMap = CommonUtil.strToMap(buyModuleCount);
		List<Object> arr = buyModuleMap.get(subModuleId + "");
		int alreadyBuyCount = 0;// 已经购买次数
		if (arr != null) {
			alreadyBuyCount = (Integer) arr.get(0);
		}
		if (alreadyBuyCount >= canBuyCount) {
			return false;
		}
		arr.add(0, alreadyBuyCount + 1);
		buyModuleCount = CommonUtil.mapToStr(buyModuleMap);
		player.setModuleBuyInfo(buyModuleCount);
		return true;
	}

}
