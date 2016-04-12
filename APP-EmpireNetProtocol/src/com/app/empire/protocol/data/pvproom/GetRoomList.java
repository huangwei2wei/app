package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetRoomList extends AbstractData {
	private int roomType;// 房间类型 1神兽副本
	public GetRoomList(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_GetRoomList, sessionId, serial);
	}
	public GetRoomList() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_GetRoomList);
	}
	public int getRoomType() {
		return roomType;
	}
	public void setRoomType(int roomType) {
		this.roomType = roomType;
	}

}
