package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class LoadCompleteOk extends AbstractData {
	public LoadCompleteOk(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_LoadCompleteOk, sessionId, serial);
	}
	public LoadCompleteOk() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_LoadCompleteOk);
	}

}
