//package com.app.empire.scene.service.drop.cmd;
//
//import org.aspectj.weaver.tools.cache.AsynchronousFileCacheBacking.AbstractCommand;
//
//import com.app.empire.protocol.Protocol;
//import com.app.empire.scene.entity.DropItemInfo;
//import com.app.empire.scene.service.campaign.Campaign;
//import com.app.empire.scene.service.campaign.CampaignMgr;
//import com.app.empire.scene.service.campaign.task.CTBaseCondition;
//import com.app.empire.scene.service.drop.objects.DropItem;
//import com.app.empire.scene.service.drop.objects.DropPackage;
//import com.app.empire.scene.service.warField.FieldMgr;
//import com.app.empire.scene.service.warField.field.Field;
//import com.app.empire.scene.service.world.ArmyProxy;
//
////@Cmd(code = Protocol.S_DROP_PICKUP_RESULT, desc = "背包添加掉落物结果")
//public class DropPickupResultCmd extends AbstractCommand {
//
//	@Override
//	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
//		// TODO Auto-generated method stub
//
//		DropPickupResultMsg req = DropPickupResultMsg.parseFrom(packet.getBytes());
//
//		if (req.getResult() == false)
//			return;
//
//		Field field = FieldMgr.getIns().getField(army.getFieldId());
//
//		field.removeDrop(req.getPackageId(), req.getDropItemId());
//
//		DropPackage drop = field.getDrop(req.getPackageId());
//		if (drop == null) {
//			return;
//		}
//		DropItem dropItem = drop.getDropItems().get(req.getDropItemId());
//		if (dropItem == null) {
//			return;
//		}
//		DropItemInfo dropItemInfo = drop.getDropItemTemplete(req.getDropItemId());
//		Campaign campaign = CampaignMgr.getCampagin(field.getCampaignId());
//		if (campaign != null) {
//			campaign.notifyTaskEvent(CTBaseCondition.GET_ITEM_COUNT, dropItemInfo.getItemId());
//		}
//
//	}
//
//}
