package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 申请进入房间
 * 
 * @author doter
 * 
 */
public class IntoRoom extends AbstractData {
	private int roomType;// 房间类型 1 神兽副本
	private int roomId;// 房间号
	private int heroId;// 英雄流水id

	public IntoRoom(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_IntoRoom, sessionId, serial);
	}
	public IntoRoom() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_IntoRoom);
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
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}

}
