package com.app.empire.scene.service.team;

import java.util.List;

import com.chuangyou.common.protobuf.pb.army.PropertyMsgProto.PropertyMsg;
import com.chuangyou.common.protobuf.pb.player.PlayerAttUpdateProto.PlayerAttUpdateMsg;
import com.app.empire.scene.constant.EnumAttr;
import com.chuangyou.xianni.proto.BroadcastUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.app.empire.scene.service.world.ArmyProxy;

public class NotifyHelper {

	/**
	 * 
	 * 同步地图信息给队友
	 * 
	 * @param t
	 */
	public static void notifyInfo(ArmyProxy army) {
		Team t = TeamMgr.getTeam(army.getPlayerId());
		if (t != null && t.getMembers().size() > 1) {
			List<Long> members = t.getMembers(army.getPlayerId());
			if (army != null) {
				PlayerAttUpdateMsg.Builder msg = PlayerAttUpdateMsg.newBuilder();
				msg.setPlayerId(army.getPlayerId());
				msg.addAtt(getPropertyMsg(army, EnumAttr.MAP_ID));
				msg.addAtt(getPropertyMsg(army, EnumAttr.MAP_KEY));
				BroadcastUtil.sendBroadcastPacket(members, Protocol.U_RESP_PLAYER_ATT_UPDATE, msg.build());
			}
		}
	}

	/**
	 * 获取更新消息
	 * 
	 * @param army
	 * @param e
	 * @return
	 */
	public static PropertyMsg.Builder getPropertyMsg(ArmyProxy army, EnumAttr e) {
		PropertyMsg.Builder att = PropertyMsg.newBuilder();
		switch (e) {
		case CUR_SOUL:
			att.setBasePoint(army.getPlayer().getCurSoul());
			att.setTotalPoint(army.getPlayer().getCurSoul());
			break;
		case MAX_SOUL:
			att.setBasePoint(army.getPlayer().getMaxSoul());
			att.setTotalPoint(army.getPlayer().getMaxSoul());
			break;
		case CUR_BLOOD:
			att.setBasePoint(army.getPlayer().getCurBlood());
			att.setTotalPoint(army.getPlayer().getCurBlood());
			break;
		case MAX_BLOOD:
			att.setBasePoint(army.getPlayer().getMaxBlood());
			att.setTotalPoint(army.getPlayer().getMaxBlood());
			break;
		case MAP_ID:
			att.setBasePoint(army.getPlayer().getField().id);
			att.setTotalPoint(army.getPlayer().getField().id);
			break;
		case MAP_KEY:
			att.setBasePoint(army.getPlayer().getField().getMapKey());
			att.setTotalPoint(army.getPlayer().getField().getMapKey());
			break;
		default:
			break;
		}
		att.setType(e.getValue());
		return att;
	}

}
