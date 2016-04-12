package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class OutRoomOk extends AbstractData {
	public OutRoomOk(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_OutRoomOk, sessionId, serial);
	}
	public OutRoomOk() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_OutRoomOk);
	}
}
