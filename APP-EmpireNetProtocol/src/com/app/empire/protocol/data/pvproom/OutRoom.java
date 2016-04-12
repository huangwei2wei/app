package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class OutRoom extends AbstractData {
	private int roomType;// 房间类型 1神兽副本
	private int roomId;// 房间号
	public OutRoom(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_OutRoom, sessionId, serial);
	}
	public OutRoom() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_OutRoom);
	}

	public int getRoomType() {
		return roomType;
	}
	public void setRoomType(int roomType) {
		this.roomType = roomType;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

}
