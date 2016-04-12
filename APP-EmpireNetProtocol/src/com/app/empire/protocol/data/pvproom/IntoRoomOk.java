package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class IntoRoomOk extends AbstractData {
	private int roomId;// 房间号

	public IntoRoomOk(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_IntoRoomOk, sessionId, serial);
	}
	public IntoRoomOk() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_IntoRoomOk);
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

}
