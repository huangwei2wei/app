package com.app.empire.scene.server.handler.warfield;

import org.apache.log4j.Logger;

import com.app.empire.protocol.pb.map.ReqChangeMapMsgProto.ReqChangeMapMsg;
import com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg;
import com.app.empire.scene.service.warField.action.EnterFieldAction;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.session.ConnectSession;
import com.app.empire.scene.util.Vector3BuilderHelper;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 进入场景
 */
public class EnterFieldHandler implements IDataHandler {
	Logger	log	= Logger.getLogger(EnterFieldHandler.class);

	@Override
	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		PbAbstractData pbData = (PbAbstractData) data;
		ReqChangeMapMsg msg = ReqChangeMapMsg.parseFrom(pbData.getBytes());
		ArmyProxy army = session.getPlayerService().getArmy(pbData.getSessionId());
		log.info("玩家请求进入场景：playerId : +" + army.getPlayerId());
		// ReqChangeMapMsg msg = ReqChangeMapMsg.parseFrom(packet.getBytes());
		PostionMsg pos = msg.getPostionMsg();

		EnterFieldAction action = new EnterFieldAction(army, pos.getMapId(), pos.getMapKey(), Vector3BuilderHelper.get(pos.getPostion()));
		army.enqueue(action);

	}

}
