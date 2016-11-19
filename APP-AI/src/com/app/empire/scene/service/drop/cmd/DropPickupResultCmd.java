package com.chuangyou.xianni.drop.cmd;

import com.chuangyou.common.protobuf.pb.drop.DropPickupResultProto.DropPickupResultMsg;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.campaign.task.CTBaseCondition;
import com.chuangyou.xianni.drop.objects.DropItem;
import com.chuangyou.xianni.drop.objects.DropPackage;
import com.chuangyou.xianni.entity.drop.DropItemInfo;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_DROP_PICKUP_RESULT, desc = "背包添加掉落物结果")
public class DropPickupResultCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub

		DropPickupResultMsg req = DropPickupResultMsg.parseFrom(packet.getBytes());

		if (req.getResult() == false)
			return;

		Field field = FieldMgr.getIns().getField(army.getFieldId());

		field.removeDrop(req.getPackageId(), req.getDropItemId());

		DropPackage drop = field.getDrop(req.getPackageId());
		if (drop == null) {
			return;
		}
		DropItem dropItem = drop.getDropItems().get(req.getDropItemId());
		if (dropItem == null) {
			return;
		}
		DropItemInfo dropItemInfo = drop.getDropItemTemplete(req.getDropItemId());
		Campaign campaign = CampaignMgr.getCampagin(field.getCampaignId());
		if (campaign != null) {
			campaign.notifyTaskEvent(CTBaseCondition.GET_ITEM_COUNT, dropItemInfo.getItemId());
		}

	}

}
