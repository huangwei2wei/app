package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class SaveTeamOk extends AbstractData {
	public SaveTeamOk(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_SaveTeamOk, sessionId, serial);
	}

	public SaveTeamOk() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_SaveTeamOk);
	}
}
