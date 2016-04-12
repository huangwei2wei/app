package com.app.empire.protocol.data.errorcode;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetSmsCodeList extends AbstractData {
	public GetSmsCodeList(int sessionId, int serial) {
		super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_GetSmsCodeList, sessionId, serial);
	}

	public GetSmsCodeList() {
		super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_GetSmsCodeList);
	}
}
