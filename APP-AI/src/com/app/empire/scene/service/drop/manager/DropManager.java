package com.chuangyou.xianni.drop.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.empire.scene.service.team.Team;
import com.app.empire.scene.service.team.TeamMgr;
import com.chuangyou.common.util.ThreadSafeRandom;
import com.chuangyou.common.util.TimeUtil;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.constant.DropItemConstant;
import com.chuangyou.xianni.drop.objects.DropItem;
import com.chuangyou.xianni.drop.objects.DropPackage;
import com.chuangyou.xianni.drop.templete.DropTempleteMgr;
import com.chuangyou.xianni.entity.drop.DropInfo;
import com.chuangyou.xianni.entity.drop.DropItemInfo;
import com.chuangyou.xianni.entity.spawn.MonsterInfo;
import com.chuangyou.xianni.role.helper.IDMakerHelper;
import com.chuangyou.xianni.role.template.MonsterInfoTemplateMgr;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

public class DropManager {

	/**
	 * 计算掉落池掉出来的物品列表
	 * @param id
	 * @return
	 */
	private static List<DropItemInfo> getDropList(int id){
		
		DropInfo pool = DropTempleteMgr.getDropPool().get(id);
		
		List<DropItemInfo> dropItems = new ArrayList<>();
		Map<Integer, DropItemInfo> items = DropTempleteMgr.getDropItemMap().get(id);
		
		ThreadSafeRandom random = new ThreadSafeRandom();
		if(pool.getType() == DropItemConstant.DropType.probability){
			for(int i = 0; i < pool.getRepeat(); i++){
				for(DropItemInfo item: items.values()){
					int rate = item.getWeight();
					if(random.isSuccessful(rate, 1000000)){
						dropItems.add(item);
					}
				}
			}
		}else if(pool.getType() == DropItemConstant.DropType.weight){
			for(int i = 0; i < pool.getRepeat(); i++){
				
				int totalWeight = 0;
				List<Integer> itemIds = new ArrayList<>();
				for(DropItemInfo item: items.values()){
					totalWeight += item.getWeight();
					itemIds.add(item.getId());
				}
				
				int randomNum = random.next(1, totalWeight);
				
				int flag = 0;
				for(int itemId: itemIds){
					DropItemInfo item = items.get(itemId);
					if(randomNum > flag && randomNum <= flag + item.getWeight()){
						dropItems.add(item);
						break;
					}
					flag += item.getWeight();
				}
			}
		}
		
		return dropItems;
	}
	
	/**
	 * 根据掉落池ID给指定玩家在指定位置掉落物品
	 * @param playerId 玩家ID
	 * @param dropRoleId 掉落者(现在只有怪物)唯一ID
	 * @param dropPoolId 掉落池模板ID
	 * @param fieldId 场景ID
	 * @param v3 位置
	 */
	public static void drop(long playerId, long dropRoleId, int dropPoolId, int fieldId, Vector3 v3){
		DropInfo pool = DropTempleteMgr.getDropPool().get(dropPoolId);
		
		if(pool.getLimitType() > 0){
			long curTime = TimeUtil.getSysCurTimeMillis();
			if(curTime < TimeUtil.getDateByString(pool.getStartTime(), pool.getLimitType()).getTime() || curTime > TimeUtil.getDateByString(pool.getEndTime(), pool.getLimitType()).getTime()){
				return;
			}
		}
		
		List<DropItemInfo> dropItems = getDropList(dropPoolId);
		
		if(dropItems.size() <= 0) return;
		
		DropPackage drop = new DropPackage();
		drop.setDropId(IDMakerHelper.dropId());
		drop.setPlayerId(playerId);
		drop.setDropRoleId(dropRoleId);
		drop.setPoolId(dropPoolId);
		drop.setV3(v3);
		drop.setDropTime((new Date()).getTime());
		drop.setDropItems(new HashMap<>());
		
		for(DropItemInfo itemInfo: dropItems){
			DropItem item = new DropItem();
			item.setId(drop.getDropId() * 10 + drop.getDropItems().size());
			item.setDropItemTempId(itemInfo.getId());
			
			drop.getDropItems().put(item.getId(), item);
		}
		
		Field field = FieldMgr.getIns().getField(fieldId);
		if(field == null) return;
		
		field.addDrop(drop);
	}
	
	/**
	 * 指定怪物掉落物品
	 * @param monsterId 怪物模板ID
	 * @param playerId 玩家ID
	 * @param dropRoleId 掉落者(现在只有怪物)唯一ID
	 * @param fieldId 场景ID
	 * @param v3 位置
	 */
	public static void dropFromMonster(int monsterId, long playerId, long dropRoleId, int fieldId, Vector3 v3){
		//自己掉落
		MonsterInfo info = MonsterInfoTemplateMgr.monsterInfoTemps.get(monsterId);
		if(info.getDrop1() > 0){
			drop(playerId, dropRoleId, info.getDrop1(), fieldId, v3);
		}
		if(info.getDrop2() > 0){
			drop(playerId, dropRoleId, info.getDrop2(), fieldId, v3);
		}
		if(info.getDrop3() > 0){
			drop(playerId, dropRoleId, info.getDrop3(), fieldId, v3);
		}
		if(info.getDrop4() > 0){
			drop(playerId, dropRoleId, info.getDrop4(), fieldId, v3);
		}
		
		//给队友掉落
		Team team = TeamMgr.getTeam(playerId);
		if(team == null) return;
		List<Long> members = team.getMembers(playerId);
		
		for(long memberId: members){
			//不在线、和自己不在同场景的成员不给掉落
			ArmyProxy army = WorldMgr.getArmy(memberId);
			if(army == null) continue;
			if(army.getFieldId() != fieldId) continue;
			
			if(info.getDrop1() > 0){
				drop(memberId, dropRoleId, info.getDrop1(), fieldId, v3);
			}
			if(info.getDrop2() > 0){
				drop(memberId, dropRoleId, info.getDrop2(), fieldId, v3);
			}
			if(info.getDrop3() > 0){
				drop(memberId, dropRoleId, info.getDrop3(), fieldId, v3);
			}
			if(info.getDrop4() > 0){
				drop(memberId, dropRoleId, info.getDrop4(), fieldId, v3);
			}
		}
	}
}
