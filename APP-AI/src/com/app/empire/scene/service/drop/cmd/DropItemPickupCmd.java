package com.chuangyou.xianni.drop.cmd;

import com.chuangyou.common.protobuf.pb.drop.DropItemPickupProto.DropItemPickupMsg;
import com.chuangyou.common.protobuf.pb.drop.DropPickupCenterProto.DropPickupCenterMsg;
import com.chuangyou.xianni.common.ErrorCode;
import com.chuangyou.xianni.common.error.ErrorMsgUtil;
import com.chuangyou.xianni.drop.objects.DropItem;
import com.chuangyou.xianni.drop.objects.DropPackage;
import com.chuangyou.xianni.entity.drop.DropItemInfo;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_DROP_PICKUP, desc = "掉落物拾取")
public class DropItemPickupCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub
		DropItemPickupMsg req = DropItemPickupMsg.parseFrom(packet.getBytes());
		
		Field field = FieldMgr.getIns().getField(army.getFieldId());
		DropPackage drop = field.getDrop(req.getPackageId());
		if(drop == null){
			ErrorMsgUtil.sendErrorMsg(army, ErrorCode.Item_IS_NOT_Existed, packet.getCode(), "物品不存在");
			return;
		}
		
		DropItem dropItem = drop.getDropItems().get(req.getDropItemId());
		if(dropItem == null){
			ErrorMsgUtil.sendErrorMsg(army, ErrorCode.Item_IS_NOT_Existed, packet.getCode(), "物品不存在");
			return;
		}
		
		DropItemInfo dropItemInfo = drop.getDropItemTemplete(req.getDropItemId());
		
		DropPickupCenterMsg.Builder msg = DropPickupCenterMsg.newBuilder();
		msg.setPackageId(req.getPackageId());
		msg.setDropItemId(req.getDropItemId());
		msg.setItemId(dropItemInfo.getItemId());
		
		msg.setCount(dropItemInfo.getCount());
		army.sendPbMessage(MessageUtil.buildMessage(Protocol.C_DROP_PICKUP, msg));
	}

}
