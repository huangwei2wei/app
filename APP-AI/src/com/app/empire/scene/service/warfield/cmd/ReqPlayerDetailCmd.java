package com.app.empire.scene.service.warfield.cmd;

import com.chuangyou.common.protobuf.pb.PlayerDetailReqMsgProto.PlayerDetailReqMsg;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

@Cmd(code = Protocol.S_REQ_PLAYER_DETAIL, desc = "请求详细数据")
public class ReqPlayerDetailCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// army.sendPacket(MessageUtil.buildMessage(Protocol.U_G_PLAYERINFO,
		// army.getCachcInfoPacket()));
		// PlayerMoveReqMsg movemsg =
		// PlayerMoveReqMsg.parseFrom(packet.getBytes());
		PlayerDetailReqMsg detailReq = PlayerDetailReqMsg.parseFrom(packet.getBytes());
		long livingId = detailReq.getLivingId();
		Field field = FieldMgr.getIns().getField(army.getFieldId());
		if (field == null) {
			return;
		}
		Living l = field.getLiving(livingId);
		if (l != null) {
			army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_G_BATTLEPLAYERINFO, l.getBattlePlayerInfoMsg()));
		}
	}

}
