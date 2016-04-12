package com.app.empire.world.server.handler.pvproom;

import com.app.empire.protocol.data.pvproom.Start;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 房主点击开始
 * 
 * @author doter
 * 
 */
public class StartHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		Start start = (Start) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int roomType = start.getRoomType();
		int roomId = start.getRoomId();
		try {
			ServiceManager.getManager().getPvpService().start(worldPlayer, roomType, roomId);
			return null;
		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
