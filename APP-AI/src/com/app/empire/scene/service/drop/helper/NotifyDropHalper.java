package com.app.empire.scene.service.drop.helper;

import java.util.List;
import java.util.Map;

import com.chuangyou.common.protobuf.pb.drop.DropItemRemoveProto.DropItemRemoveMsg;
import com.chuangyou.xianni.constant.DropItemConstant;
import com.chuangyou.xianni.drop.objects.DropPackage;
import com.chuangyou.xianni.drop.templete.DropTempleteMgr;
import com.chuangyou.xianni.entity.drop.DropInfo;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

public class NotifyDropHalper {

	public static void notifyAddDropPackage(DropPackage drop) {
		ArmyProxy army = WorldMgr.getArmy(drop.getPlayerId());
		if (army == null) {
			return;
		}
		short actionType = DropItemConstant.notifyAction.addDrop;
		if (drop.getDropRoleId() == -1) {
			actionType = DropItemConstant.notifyAction.synchronizationDrop;
		}

		Field field = FieldMgr.getIns().getField(army.getFieldId());

		DropInfo dropPoolTemp = DropTempleteMgr.getDropPool().get(drop.getPoolId());
		army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_PACKAGE, drop.buildProto(actionType)));
		if (dropPoolTemp.getVisibleType() == DropItemConstant.VisibleType.publicVisible) {
			List<Long> players = field.getLivings();
			for (long id : players) {
				ArmyProxy mapArmy = WorldMgr.getArmy(id);
				mapArmy.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_PACKAGE, drop.buildProto(actionType)));
			}
		}
	}

	/**
	 * 玩家进入地图时通知该玩家当前地图所有公共可见的掉落物
	 * 
	 * @param field
	 * @param living
	 */
	public static void notifyPlayerFieldDropItems(Field field, Living living) {
		Map<Integer, DropPackage> dropItems = field.getDropItems();
		for (DropPackage drop : dropItems.values()) {
			if (drop.getPlayerId() == living.getArmyId() || drop.getDropTemplete().getVisibleType() == DropItemConstant.VisibleType.publicVisible) {
				ArmyProxy army = WorldMgr.getArmy(living.getArmyId());
				army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_PACKAGE, drop.buildProto(DropItemConstant.notifyAction.synchronizationDrop)));
			}
		}
	}

	/**
	 * 删除单个掉落物
	 * 
	 * @param field
	 * @param dropId
	 * @param dropItemId
	 */
	public static void notifyRemoveDropItem(Field field, int dropId, long dropItemId) {
		DropPackage drop = field.getDrop(dropId);
		ArmyProxy army = WorldMgr.getArmy(drop.getPlayerId());

		DropItemRemoveMsg.Builder msg = DropItemRemoveMsg.newBuilder();
		msg.setPackageId(dropId);
		msg.setDropItemId(dropItemId);
		army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_REMOVE, msg));

		DropInfo dropTemp = DropTempleteMgr.getDropPool().get(drop.getPoolId());
		if (dropTemp.getVisibleType() == DropItemConstant.VisibleType.publicVisible) {
			List<Long> players = field.getLivings();
			for (long id : players) {
				ArmyProxy mapArmy = WorldMgr.getArmy(id);
				DropItemRemoveMsg.Builder mapMsg = DropItemRemoveMsg.newBuilder();
				mapMsg.setPackageId(dropId);
				mapMsg.setDropItemId(dropItemId);
				mapArmy.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_REMOVE, mapMsg));
			}
		}
	}

	/**
	 * 删除整个掉落包
	 * 
	 * @param field
	 * @param dropId
	 */
	public static void notifyRemoveDropPackage(Field field, DropPackage drop) {
		ArmyProxy army = WorldMgr.getArmy(drop.getPlayerId());

		if (army == null)
			return;
		DropItemRemoveMsg.Builder msg = DropItemRemoveMsg.newBuilder();
		msg.setPackageId(drop.getDropId());
		msg.setDropItemId(0);
		army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_REMOVE, msg));

		DropInfo dropTemp = DropTempleteMgr.getDropPool().get(drop.getPoolId());
		if (dropTemp.getVisibleType() == DropItemConstant.VisibleType.publicVisible) {
			List<Long> players = field.getLivings();
			for (long id : players) {
				ArmyProxy mapArmy = WorldMgr.getArmy(id);
				DropItemRemoveMsg.Builder mapMsg = DropItemRemoveMsg.newBuilder();
				mapMsg.setPackageId(drop.getDropId());
				mapMsg.setDropItemId(0);
				mapArmy.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_REMOVE, mapMsg));
			}
		}
	}
}
