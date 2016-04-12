package com.app.empire.protocol.data.account;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class UpdatePlayerNameOk extends AbstractData {
	public UpdatePlayerNameOk(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_UpdatePlayerNameOk, sessionId, serial);
	}
	public UpdatePlayerNameOk() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_UpdatePlayerNameOk);
	}

}
