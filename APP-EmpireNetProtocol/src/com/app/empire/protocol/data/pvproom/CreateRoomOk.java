package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 创建房间成功
 * 
 * @author doter
 * 
 */
public class CreateRoomOk extends AbstractData {
	private int roomId;// 房间号
	public CreateRoomOk(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_CreateRoomOk, sessionId, serial);
	}
	public CreateRoomOk() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_CreateRoomOk);
	}

	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

}
