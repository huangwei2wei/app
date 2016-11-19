package com.app.empire.scene.service.warfield.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.chuangyou.common.protobuf.pb.army.PropertyMsgProto.PropertyMsg;
import com.chuangyou.common.protobuf.pb.player.PlayerAttUpdateProto.PlayerAttUpdateMsg;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.helper.NotifyNearHelper;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

@Cmd(code = Protocol.S_PROPERTY_UPDATE, desc = "玩家属性更新")
public class PlayerPropertyUpdateCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		// TODO Auto-generated method stub

		PlayerAttUpdateMsg req = PlayerAttUpdateMsg.parseFrom(packet.getBytes());

		// 改变属性的玩家
		ArmyProxy pArmy = WorldMgr.getArmy(req.getPlayerId());

		if (pArmy != null) {
			// 修改玩家属性
			List<PropertyMsg> attList = req.getAttList();

			attList = new ArrayList<>(attList);
			pArmy.getPlayer().readProperty(attList);

			PlayerAttUpdateMsg.Builder newpacket = PlayerAttUpdateMsg.newBuilder();
			newpacket.setPlayerId(req.getPlayerId());
			newpacket.addAllAtt(attList);
			// 通知自己
			PBMessage selfPkg = MessageUtil.buildMessage(Protocol.U_RESP_PLAYER_ATT_UPDATE, newpacket);
			pArmy.sendPbMessage(selfPkg);

			// 部分角色属性需要同步给周围玩家
			boolean needNotify = false;
			PlayerAttUpdateMsg.Builder notifyMsg = PlayerAttUpdateMsg.newBuilder();
			for (PropertyMsg property : attList) {
				if (property.getType() == EnumAttr.Mount.getValue()) {
					PropertyMsg.Builder speedMsg = PropertyMsg.newBuilder();
					speedMsg.setType(EnumAttr.SPEED.getValue());
					speedMsg.setTotalPoint(pArmy.getPlayer().getProperty(EnumAttr.SPEED.getValue()));
					notifyMsg.addAtt(speedMsg);
				}
			}
			if (needNotify == true) {
				notifyMsg.setPlayerId(pArmy.getPlayerId());

				// 通知附近玩家
				Set<Long> nears = pArmy.getPlayer().getNears(new PlayerSelectorHelper(pArmy.getPlayer()));
				NotifyNearHelper.notifyAttrChange(pArmy, nears, req);
			} else {
				notifyMsg.clear();
				notifyMsg = null;
			}
		}
	}

}
