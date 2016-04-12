package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 加载成功
 * 
 * @author doter
 * 
 */
public class LoadComplete extends AbstractData {
	private int roomType;// 房间类型 1神兽副本
	private int roomId;// 房间号

	public LoadComplete(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_LoadComplete, sessionId, serial);
	}
	public LoadComplete() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_LoadComplete);
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
