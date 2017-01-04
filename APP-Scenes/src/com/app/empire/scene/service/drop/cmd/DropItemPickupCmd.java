//package com.app.empire.scene.service.drop.cmd;
//
//import org.aspectj.bridge.MessageUtil;
//import org.aspectj.weaver.tools.cache.AsynchronousFileCacheBacking.AbstractCommand;
//
//import com.app.empire.protocol.Protocol;
//import com.app.empire.scene.constant.CommonType.CurrencyItemType;
//import com.app.empire.scene.constant.ErrorCode;
//import com.app.empire.scene.entity.DropItemInfo;
//import com.app.empire.scene.service.drop.objects.DropItem;
//import com.app.empire.scene.service.drop.objects.DropPackage;
//import com.app.empire.scene.service.warField.FieldMgr;
//import com.app.empire.scene.service.warField.field.Field;
//import com.app.empire.scene.service.world.ArmyProxy;
//import com.app.empire.scene.util.ErrorMsgUtil;
//
////@Cmd(code = Protocol.S_DROP_PICKUP, desc = "掉落物拾取")
//public class DropItemPickupCmd extends AbstractCommand {
//
//	@Override
//	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
//		// TODO Auto-generated method stub
//		DropItemPickupMsg req = DropItemPickupMsg.parseFrom(packet.getBytes());
//
//		Field field = FieldMgr.getIns().getField(army.getFieldId());
//		DropPackage drop = field.getDrop(req.getPackageId());
//		if (drop == null) {
//			ErrorMsgUtil.sendErrorMsg(army, ErrorCode.Item_IS_NOT_Existed, packet.getCode(), "物品不存在");
//			return;
//		}
//		
//
//		DropItem dropItem = drop.getDropItems().get(req.getDropItemId());
//		if (dropItem == null) {
//			ErrorMsgUtil.sendErrorMsg(army, ErrorCode.Item_IS_NOT_Existed, packet.getCode(), "物品不存在");
//			return;
//		}
//
//		DropItemInfo dropItemInfo = drop.getDropItemTemplete(req.getDropItemId());
//
//		DropPickupCenterMsg.Builder msg = DropPickupCenterMsg.newBuilder();
//		msg.setPackageId(req.getPackageId());
//		msg.setDropItemId(req.getDropItemId());
//		msg.setItemId(dropItemInfo.getItemId());
//
//		// 插入代码：巨富禁制，提升灵石掉落数量
//		int dropCount = dropItemInfo.getCount();
//		if (dropItemInfo.getItemId() == CurrencyItemType.MONEY_ITEM) {
//			MagicwpCompanent companent = army.getPlayer().getMagicwpCompanent(MagicwpBanConstant.ADD_GOLD);
//			if (companent != null && companent.isEffect()) {
//				dropCount = dropCount + companent.getEffectValue() * dropCount / 100;
//			}
//		}
//		msg.setCount(dropItemInfo.getCount());
//		army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_DROP_PICKUP, msg));
//	}
//
//}
