package com.app.empire.world.server.handler.map;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.map.ReqChangeMapMsgProto.ReqChangeMapMsg;
import com.app.empire.world.entity.mongo.PlayerPostion;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 用户请求变更地图
 */
public class ChangeMapHandler implements IDataHandler {

	public AbstractData handle(AbstractData data) throws Exception {
		PbAbstractData pbData = (PbAbstractData) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		// 玩家当前位置信息
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		PlayerPostion pinfo = worldPlayer.getPlayer().getPostion();

		// 玩家目标位置信息
		ReqChangeMapMsg msg = ReqChangeMapMsg.parseFrom(pbData.getBytes());
		// TODO 判断，是否允许玩家切换场景

		// TODO 向场景服务器申请场景进入
		// PBMessage message = MessageUtil.buildMessage(Protocol.S_ENTERSCENE, player.getPlayerId(), msg);
		// player.sendPbMessage(message);
		session.write(Protocol.MAIN_MAP, Protocol.MAP_ChangeMap, data.getSessionId(), data.getSerial(), msg, EnumTarget.SCENESSERVER.getValue());
		return null;
	}

}
