package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 返回房间列表
 * 
 * @author doter
 * 
 */
public class GetRoomListOk extends AbstractData {
	private int[] roomId;// 房间号
	private int[] playerNum;// 已有房间人数

	public GetRoomListOk(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_GetRoomListOk, sessionId, serial);
	}
	public GetRoomListOk() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_GetRoomListOk);
	}
	public int[] getRoomId() {
		return roomId;
	}
	public void setRoomId(int[] roomId) {
		this.roomId = roomId;
	}
	public int[] getPlayerNum() {
		return playerNum;
	}
	public void setPlayerNum(int[] playerNum) {
		this.playerNum = playerNum;
	}

}
