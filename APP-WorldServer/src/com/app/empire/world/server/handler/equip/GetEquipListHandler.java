package com.app.empire.world.server.handler.equip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.app.empire.protocol.data.equip.GetEquipList;
import com.app.empire.protocol.data.equip.GetEquipListOk;
import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.entity.mongo.HeroEquipGoods;
import com.app.empire.world.entity.mongo.PlayerHeroEquip;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取英雄装备列表
 * 
 * @since JDK 1.7
 */
public class GetEquipListHandler implements IDataHandler {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(GetEquipListHandler.class);

	public AbstractData handle(AbstractData data) throws Exception {
		GetEquipList getEquipList = (GetEquipList) data;
		Integer[] heroIds = ArrayUtils.toObject(getEquipList.getHeroId());
		List<Integer> heroIdList = Arrays.asList(heroIds);
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		List<PlayerHeroEquip> equipList = ServiceManager.getManager().getPlayerEquipService().getHeroEquipList(worldPlayer, heroIdList);

		// private int[] heroId;// 英雄流水id
		// private int[] rank; // 当前军衔阶段
		// private int[] achieveProAdd; // 装备成就属性加成
		// private String[] achieve; // 装备成就格式 1:3:6,2:3:6
		// private int[] equipNo;// 装备栏编码
		// private int[] equipId; // 装备id
		// private int[] equipExp; // 物品精炼经验
		// private int[] equipStar; // 物品星级
		// private int[] equipQuality; // 物品品质
		// private String[] equipPro; // 物品属性
		// private int[] proAdd;// 精炼属性加成

		List<Integer> heroId = new ArrayList<Integer>();// 英雄流水id
		List<Integer> rank = new ArrayList<Integer>();// 当前军衔阶段
		List<Integer> achieveProAdd = new ArrayList<Integer>();// 装备成就属性加成
		List<String> achieve = new ArrayList<String>();// 装备成就格式 1:3:6,2:3:6
		List<Integer> equipNo = new ArrayList<Integer>();// 装备栏编码
		List<Integer> equipId = new ArrayList<Integer>();// 装备id
		List<Integer> equipExp = new ArrayList<Integer>();// 物品精炼经验
		List<Integer> equipStar = new ArrayList<Integer>();// 物品星级
		List<Integer> equipQuality = new ArrayList<Integer>();// 物品品质
		List<String> equipPro = new ArrayList<String>();// 物品属性
		List<Integer> proAdd = new ArrayList<Integer>();// 精炼属性加成
		for (PlayerHeroEquip playerHeroEquip : equipList) {
			Map<Integer, HeroEquipGoods> heroEquipGoods = playerHeroEquip.getEquip();
			Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
			map.put("1", playerHeroEquip.getAchieve());
			map.put("2", playerHeroEquip.getAchieve2());
			String achieveStr = CommonUtil.mapToStrForInt(map);
			for (Entry<Integer, HeroEquipGoods> heroEquip : heroEquipGoods.entrySet()) {
				heroId.add(playerHeroEquip.getHeroId());
				rank.add(playerHeroEquip.getRank());
				achieveProAdd.add(playerHeroEquip.getAchieveProAdd());
				achieve.add(achieveStr);// 装备成就格式 1:3:6,2:3:6
				equipNo.add(heroEquip.getKey());
				HeroEquipGoods equip = heroEquip.getValue();
				equipId.add(equip.getGoodsId());
				equipExp.add(equip.getGoodsExp()); // 物品升星经验
				equipStar.add(equip.getGoodsStar()); // 物品星级
				equipPro.add(equip.getProperty());// 物品属性
				equipQuality.add(equip.getGoodsQuality()); // 物品品质
				proAdd.add(equip.getProAdd()); // 精炼属性加成
			}
		}
		GetEquipListOk listOk = new GetEquipListOk(data.getSessionId(), data.getSerial());
		listOk.setHeroId(ArrayUtils.toPrimitive(heroId.toArray(new Integer[heroId.size()])));
		listOk.setRank(ArrayUtils.toPrimitive(rank.toArray(new Integer[rank.size()])));
		listOk.setAchieveProAdd(ArrayUtils.toPrimitive(achieveProAdd.toArray(new Integer[achieveProAdd.size()])));
		listOk.setAchieve(achieve.toArray(new String[achieve.size()]));
		listOk.setEquipNo(ArrayUtils.toPrimitive(equipNo.toArray(new Integer[equipNo.size()])));
		listOk.setEquipId(ArrayUtils.toPrimitive(equipId.toArray(new Integer[equipId.size()])));
		listOk.setEquipExp(ArrayUtils.toPrimitive(equipExp.toArray(new Integer[equipExp.size()])));
		listOk.setEquipStar(ArrayUtils.toPrimitive(equipStar.toArray(new Integer[equipStar.size()])));
		listOk.setEquipQuality(ArrayUtils.toPrimitive(equipQuality.toArray(new Integer[equipQuality.size()])));
		listOk.setEquipPro(equipPro.toArray(new String[equipPro.size()]));
		listOk.setProAdd(ArrayUtils.toPrimitive(proAdd.toArray(new Integer[proAdd.size()])));
		return listOk;
	}
}