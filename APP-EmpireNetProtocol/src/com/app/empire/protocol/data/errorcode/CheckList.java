package com.app.empire.protocol.data.errorcode;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class CheckList extends AbstractData {

	public CheckList(int sessionId, int serial) {
		super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_CheckList, sessionId, serial);
	}

	public CheckList() {
		super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_CheckList);
	}
}
