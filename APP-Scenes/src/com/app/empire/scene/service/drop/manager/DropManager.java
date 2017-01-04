package com.app.empire.scene.service.drop.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.DropInfo;
import com.app.db.mysql.entity.DropItemInfo;
import com.app.db.mysql.entity.MonsterInfo;
import com.app.empire.scene.constant.DropItemConstant;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.drop.helper.random.WeightRandomUtil;
import com.app.empire.scene.service.drop.objects.DropItem;
import com.app.empire.scene.service.drop.objects.DropPackage;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.util.ThreadSafeRandom;
import com.app.empire.scene.util.TimeUtil;
import com.app.empire.scene.util.Vector3;

public class DropManager {
	protected static Logger log = Logger.getLogger(DropManager.class);

	/**
	 * 计算掉落池掉出来的物品列表
	 * 
	 * @param id
	 * @return
	 */
	public static List<DropItemInfo> getDropList(int id) {

		DropInfo pool = ServiceManager.getManager().getGameConfigService().getDropPool().get(id);

		List<DropItemInfo> dropItems = new ArrayList<>();
		Map<Integer, DropItemInfo> items = ServiceManager.getManager().getGameConfigService().getDropItemMap().get(id);

		ThreadSafeRandom random = new ThreadSafeRandom();
		if (pool.getType() == DropItemConstant.DropType.PROBABILITY) {
			for (int i = 0; i < pool.getRepeat(); i++) {
				for (DropItemInfo item : items.values()) {
					int rate = item.getWeight();
					if (random.isSuccessful(rate, 1000000)) {
						dropItems.add(item);
					}
				}
			}
		} else if (pool.getType() == DropItemConstant.DropType.WEIGHT) {
			for (int i = 0; i < pool.getRepeat(); i++) {

				List<DropItemInfo> itemList = new ArrayList<>();
				itemList.addAll(items.values());

				DropItemInfo resultItem = WeightRandomUtil.getRandomWeight(itemList);
				if (resultItem != null) {
					dropItems.add(resultItem);
				}
			}
		} else if (pool.getType() == DropItemConstant.DropType.SPECIAL_WEIGHT) {
			for (int i = 0; i < pool.getRepeat(); i++) {

				List<DropItemInfo> itemList = new ArrayList<>();
				itemList.addAll(items.values());

				DropItemInfo resultItem = WeightRandomUtil.getRandomWeight(pool.getTotalWeight(), itemList);
				if (resultItem != null) {
					dropItems.add(resultItem);
				}
			}
		}

		return dropItems;
	}

	/**
	 * 根据掉落池ID给指定玩家在指定位置掉落物品
	 * 
	 * @param playerId 玩家ID
	 * @param dropRoleId 掉落者(现在只有怪物)唯一ID
	 * @param dropPoolId 掉落池模板ID
	 * @param fieldId 场景ID
	 * @param v3 位置
	 */
	public static void drop(int playerId, long dropRoleId, int dropPoolId, int fieldId, Vector3 v3) {
		DropInfo pool =ServiceManager.getManager().getGameConfigService().getDropPool().get(dropPoolId);
		if (pool == null) {
			log.error("DropInfo pool is null,dropId :" + dropPoolId);
			return;
		}
		if (pool.getLimitType() > 0) {
			long curTime = TimeUtil.getSysCurTimeMillis();
			if (!TimeUtil.isInTime(curTime, pool.getLimitType(), pool.getStartTime(), pool.getEndTime())) {
				return;
			}
		}

		List<DropItemInfo> dropItems = getDropList(dropPoolId);

		if (dropItems.size() <= 0)
			return;

		DropPackage drop = new DropPackage();
		drop.setDropId(IDMakerHelper.dropId());
		drop.setPlayerId(playerId);
		drop.setDropRoleId(dropRoleId);
		drop.setPoolId(dropPoolId);
		drop.setV3(v3);
		drop.setDropTime((new Date()).getTime());
		drop.setDropItems(new HashMap<Long, DropItem>());

		for (DropItemInfo itemInfo : dropItems) {
			DropItem item = new DropItem();
			item.setId(drop.getDropId() * 10 + drop.getDropItems().size());
			item.setDropItemTempId(itemInfo.getId());

			drop.getDropItems().put(item.getId(), item);
		}

		Field field = FieldMgr.getIns().getField(fieldId);
		if (field == null)
			return;

		field.addDrop(drop);
	}

	/**
	 * 指定怪物掉落物品
	 * 
	 * @param monsterId 怪物模板ID
	 * @param playerId 玩家ID
	 * @param dropRoleId 掉落者(现在只有怪物)唯一ID
	 * @param fieldId 场景ID
	 * @param v3 位置
	 */
	public static void dropFromMonster(int monsterId, int playerId, long dropRoleId, int fieldId, Vector3 v3) {
		// 自己掉落
		MonsterInfo info =ServiceManager.getManager().getGameConfigService().getMonsterInfoTemps().get(monsterId);
		if (info.getDrop1() > 0) {
			drop(playerId, dropRoleId, info.getDrop1(), fieldId, v3);
		}
		if (info.getDrop2() > 0) {
			drop(playerId, dropRoleId, info.getDrop2(), fieldId, v3);
		}
		if (info.getDrop3() > 0) {
			drop(playerId, dropRoleId, info.getDrop3(), fieldId, v3);
		}
		if (info.getDrop4() > 0) {
			drop(playerId, dropRoleId, info.getDrop4(), fieldId, v3);
		}

		// 给队友掉落
//		Team team = TeamMgr.getTeamByPlayerId(playerId);
//		if (team == null)
//			return;
//		List<Integer> members = team.getMembers(playerId);
//
//		for (int memberId : members) {
//			// 不在线、和自己不在同场景的成员不给掉落
//			ArmyProxy army = WorldMgr.getArmy(memberId);
//			if (army == null)
//				continue;
//			if (army.getFieldId() != fieldId)
//				continue;
//
//			if (info.getDrop1() > 0) {
//				drop(memberId, dropRoleId, info.getDrop1(), fieldId, v3);
//			}
//			if (info.getDrop2() > 0) {
//				drop(memberId, dropRoleId, info.getDrop2(), fieldId, v3);
//			}
//			if (info.getDrop3() > 0) {
//				drop(memberId, dropRoleId, info.getDrop3(), fieldId, v3);
//			}
//			if (info.getDrop4() > 0) {
//				drop(memberId, dropRoleId, info.getDrop4(), fieldId, v3);
//			}
//		}
	}
}
