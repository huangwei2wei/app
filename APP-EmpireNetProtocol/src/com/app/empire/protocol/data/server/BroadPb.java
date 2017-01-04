package com.app.empire.protocol.data.server;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class BroadPb extends AbstractData {
	private int[] playerId;// 要广播的对象
	private byte[] data; // 数据

	public BroadPb(int sessionId, int serial) {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_BroadPb, sessionId, serial);
	}

	public BroadPb() {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_BroadPb);
	}

	public int[] getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int[] playerId) {
		this.playerId = playerId;
	}

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
