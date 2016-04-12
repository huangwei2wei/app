package com.app.empire.protocol.data.mail;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class DelMailOk extends AbstractData {

	public DelMailOk(int sessionId, int serial) {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_DelMailOk, sessionId, serial);
	}

	public DelMailOk() {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_DelMailOk);
	}

}
