package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 开始
 * 
 * @author doter
 * 
 */

public class Start extends AbstractData {
	private int roomType;// 房间类型 1神兽副本
	private int roomId;// 房间号
	public Start(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_Start, sessionId, serial);
	}
	public Start() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_Start);
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
