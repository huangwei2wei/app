package com.app.empire.world.server.handler.pvproom;

import java.util.List;

import com.app.empire.protocol.data.pvproom.GetRoomList;
import com.app.empire.protocol.data.pvproom.GetRoomListOk;
import com.app.empire.world.model.RoomVo;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取房间列表
 * 
 * @author doter
 * 
 */
public class GetRoomListHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		GetRoomList getRoomList = (GetRoomList) data;
		int roomType = getRoomList.getRoomType();
		List<RoomVo> roomList = ServiceManager.getManager().getPvpService().getRoomList(roomType);
		int size = roomList.size();
		int[] roomId = new int[size];
		int[] playerNum = new int[size];
		for (int i = 0; i < size; i++) {
			roomId[i] = roomList.get(i).getRoomId();
			playerNum[i] = roomList.get(i).getPlayerCount();
		}
		GetRoomListOk ok = new GetRoomListOk(data.getSessionId(), data.getSerial());
		ok.setRoomId(roomId);
		ok.setPlayerNum(playerNum);
		return ok;
	}
}
