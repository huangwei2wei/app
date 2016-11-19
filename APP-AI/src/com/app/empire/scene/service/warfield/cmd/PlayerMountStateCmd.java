package com.app.empire.scene.service.warfield.cmd;

import java.util.Set;

import com.chuangyou.common.protobuf.pb.army.PropertyMsgProto.PropertyMsg;
import com.chuangyou.common.protobuf.pb.player.PlayerAttUpdateProto.PlayerAttUpdateMsg;
import com.chuangyou.common.protobuf.pb.player.PlayerMountStateReqProto.PlayerMountStateReqMsg;
import com.chuangyou.common.protobuf.pb.player.PlayerMountStateRespProto.PlayerMountStateRespMsg;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.proto.BroadcastUtil;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.helper.NotifyNearHelper;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_PLAYER_MOUNT_STATE_REQ, desc = "请求上下坐骑")
public class PlayerMountStateCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub

		PlayerMountStateReqMsg req = PlayerMountStateReqMsg.parseFrom(packet.getBytes());
		if(req.getPlayerId() != army.getPlayerId()) return;
		
		if(army.getPlayer().getMountState() != req.getMountState()){
			
			/**         上坐骑验证,规则出来以后添加            */
			
			army.getPlayer().setMountState(req.getMountState());
			
			//通知自己
			PlayerMountStateRespMsg.Builder msg = PlayerMountStateRespMsg.newBuilder();
			msg.setPlayerId(army.getPlayerId());
			msg.setMountState(army.getPlayer().getMountState());
			PBMessage pmsg = MessageUtil.buildMessage(Protocol.U_PLAYER_MOUNT_STATE_RESP, msg);
			army.sendPbMessage(pmsg);
			
			//通知附近玩家
			Set<Long> nears = army.getPlayer().getNears(new PlayerSelectorHelper(army.getPlayer()));
			BroadcastUtil.sendBroadcastPacket(nears, Protocol.U_PLAYER_MOUNT_STATE_RESP, msg.build());
			
			//上下坐骑影响速度
			PlayerAttUpdateMsg.Builder attMsg = PlayerAttUpdateMsg.newBuilder();
			attMsg.setPlayerId(army.getPlayerId());
			PropertyMsg.Builder property = PropertyMsg.newBuilder();
			property.setType(EnumAttr.SPEED.getValue());
			property.setTotalPoint(army.getPlayer().getProperty(EnumAttr.SPEED.getValue()));
			attMsg.addAtt(property);
			
			PBMessage selfPkg = MessageUtil.buildMessage(Protocol.U_RESP_PLAYER_ATT_UPDATE, attMsg);
			army.sendPbMessage(selfPkg);
			
			// 通知附近玩家
			NotifyNearHelper.notifyAttrChange(army, nears, attMsg.build());
		}
	}

}
