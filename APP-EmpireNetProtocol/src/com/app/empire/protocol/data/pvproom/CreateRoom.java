package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class CreateRoom extends AbstractData {
	private int roomType;// 房间类型
	private int heroId;// 进入房间的英雄流水id

	public CreateRoom(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_CreateRoom, sessionId, serial);
	}
	public CreateRoom() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_CreateRoom);
	}

	public int getRoomType() {
		return roomType;
	}
	public void setRoomType(int roomType) {
		this.roomType = roomType;
	}
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}

}
