package com.app.empire.world.server.handler.pvproom;

import com.app.empire.protocol.data.pvproom.OutRoom;
import com.app.empire.protocol.data.pvproom.OutRoomOk;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;
/**
 * 退出房间
 * 
 * @author doter
 * 
 */
public class OutRoomHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		OutRoom intoRoom = (OutRoom) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int roomType = intoRoom.getRoomType();
		int roomId = intoRoom.getRoomId();
		ServiceManager.getManager().getPvpService().outRoom(worldPlayer, roomType, roomId);
		OutRoomOk outRoomOk = new OutRoomOk(data.getSessionId(), data.getSerial());
		return outRoomOk;
	}
}
