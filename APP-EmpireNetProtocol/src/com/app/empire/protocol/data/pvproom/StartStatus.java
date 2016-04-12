package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class StartStatus extends AbstractData {
	private int status;// 状态 1、房主点击开始，2 、所有玩家已经就绪进入战斗

	public StartStatus(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_StartStatus, sessionId, serial);
	}
	public StartStatus() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_StartStatus);
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
