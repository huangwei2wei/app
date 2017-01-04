package com.app.empire.scene.service.drop.helper;

import java.util.List;
import java.util.Map;

import com.app.db.mysql.entity.DropInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.drop.DropItemRemoveProto.DropItemRemoveMsg;
import com.app.empire.scene.constant.DropItemConstant;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.drop.objects.DropPackage;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.service.world.PlayerService;
import com.google.protobuf.Message;

public class NotifyDropHalper {

	public static void notifyAddDropPackage(DropPackage drop) {
		ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(drop.getPlayerId());
		if (army == null) {
			return;
		}
		short actionType = DropItemConstant.notifyAction.ADDDROP;
		if (drop.getDropRoleId() == -1) {
			actionType = DropItemConstant.notifyAction.SYNCHRONIZATIONDROP;
		}

		Field field = FieldMgr.getIns().getField(army.getFieldId());

		DropInfo dropPoolTemp =ServiceManager.getManager().getGameConfigService().getDropPool().get(drop.getPoolId());
		Message msg = drop.buildProto(actionType).build();
		army.sendPbMessage(Protocol.MAIN_DROP, Protocol.DROP_ItemPackage, msg);
		if (dropPoolTemp.getVisibleType() == DropItemConstant.VisibleType.PUBLICVISIBLE) {
			List<Integer> players = field.getPlayers();
			for (int id : players) {
				ArmyProxy mapArmy = ServiceManager.getManager().getPlayerService().getArmy(id);
				mapArmy.sendPbMessage(Protocol.MAIN_DROP, Protocol.DROP_ItemPackage, msg);
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
			if (drop.getPlayerId() == living.getArmyId() || drop.getDropTemplete().getVisibleType() == DropItemConstant.VisibleType.PUBLICVISIBLE) {
				ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(living.getArmyId());
				army.sendPbMessage(Protocol.MAIN_DROP, Protocol.DROP_ItemPackage, drop.buildProto(DropItemConstant.notifyAction.SYNCHRONIZATIONDROP).build());
				// army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_PACKAGE, drop.buildProto(DropItemConstant.notifyAction.SYNCHRONIZATIONDROP)));
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
		ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(drop.getPlayerId());

		DropItemRemoveMsg.Builder msg = DropItemRemoveMsg.newBuilder();
		msg.setPackageId(dropId);
		msg.setDropItemId(dropItemId);
		army.sendPbMessage(Protocol.MAIN_DROP, Protocol.DROP_ItemRemove, msg.build());

		DropInfo dropTemp =ServiceManager.getManager().getGameConfigService().getDropPool().get(drop.getPoolId());
		if (dropTemp.getVisibleType() == DropItemConstant.VisibleType.PUBLICVISIBLE) {
			List<Integer> players = field.getPlayers();
			for (int id : players) {
				ArmyProxy mapArmy = ServiceManager.getManager().getPlayerService().getArmy(id);
				DropItemRemoveMsg.Builder mapMsg = DropItemRemoveMsg.newBuilder();
				mapMsg.setPackageId(dropId);
				mapMsg.setDropItemId(dropItemId);
				mapArmy.sendPbMessage(Protocol.MAIN_DROP, Protocol.DROP_ItemRemove, mapMsg.build());
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
		ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(drop.getPlayerId());

		if (army == null)
			return;
		DropItemRemoveMsg.Builder msg = DropItemRemoveMsg.newBuilder();
		msg.setPackageId(drop.getDropId());
		msg.setDropItemId(0);
		// army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_REMOVE, msg));
		army.sendPbMessage(Protocol.MAIN_DROP, Protocol.DROP_ItemRemove, msg.build());

		DropInfo dropTemp =ServiceManager.getManager().getGameConfigService().getDropPool().get(drop.getPoolId());
		if (dropTemp.getVisibleType() == DropItemConstant.VisibleType.PUBLICVISIBLE) {
			List<Integer> players = field.getPlayers();
			for (int id : players) {
				ArmyProxy mapArmy = ServiceManager.getManager().getPlayerService().getArmy(id);
				DropItemRemoveMsg.Builder mapMsg = DropItemRemoveMsg.newBuilder();
				mapMsg.setPackageId(drop.getDropId());
				mapMsg.setDropItemId(0);
				mapArmy.sendPbMessage(Protocol.MAIN_DROP, Protocol.DROP_ItemRemove, mapMsg.build());
				// mapArmy.sendPbMessage(MessageUtil.buildMessage(Protocol.U_DROP_ITEM_REMOVE, mapMsg));
			}
		}
	}
}
