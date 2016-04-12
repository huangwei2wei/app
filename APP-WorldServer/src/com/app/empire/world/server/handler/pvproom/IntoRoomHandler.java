package com.app.empire.world.server.handler.pvproom;
import org.apache.log4j.Logger;

import com.app.empire.protocol.data.pvproom.IntoRoom;
import com.app.empire.protocol.data.pvproom.IntoRoomOk;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.RoomVo;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 进入房间
 * 
 * @author doter
 * 
 */

public class IntoRoomHandler implements IDataHandler {
	Logger log = Logger.getLogger(IntoRoomHandler.class);
	public AbstractData handle(AbstractData data) throws Exception {
		IntoRoom intoRoom = (IntoRoom) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int roomType = intoRoom.getRoomType();
		int roomId = intoRoom.getRoomId();
		int heroId = intoRoom.getHeroId();
		try {
			RoomVo room = ServiceManager.getManager().getPvpService().intoRoom(worldPlayer, heroId, roomType, roomId);
			IntoRoomOk intoRoomOk = new IntoRoomOk(data.getSessionId(), data.getSerial());
			intoRoomOk.setRoomId(room.getRoomId());
			return intoRoomOk;
		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
