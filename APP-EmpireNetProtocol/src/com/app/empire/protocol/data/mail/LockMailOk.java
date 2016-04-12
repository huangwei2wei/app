package com.app.empire.protocol.data.mail;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class LockMailOk extends AbstractData {
	public LockMailOk(int sessionId, int serial) {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_LockMailOk, sessionId, serial);
	}
	public LockMailOk() {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_LockMailOk);
	}

}
