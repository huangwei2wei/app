package com.app.empire.world.server.handler.pvproom;

import com.app.empire.protocol.data.pvproom.CreateRoom;
import com.app.empire.protocol.data.pvproom.CreateRoomOk;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.RoomVo;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 创建 pvp 房间
 * 
 * @author doter
 * 
 */
public class CreateRoomHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		CreateRoom createRoom = (CreateRoom) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int roomType = createRoom.getRoomType();
		int heroId = createRoom.getHeroId();
		try {
			RoomVo room = ServiceManager.getManager().getPvpService().createRoom(worldPlayer, heroId, roomType);
			CreateRoomOk ok = new CreateRoomOk(data.getSessionId(), data.getSerial());
			ok.setRoomId(room.getRoomId());
			return ok;
		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
