package com.app.empire.protocol.data.errorlog;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class SendLogList extends AbstractData {
	private String[] loglist;
	public SendLogList(int sessionId, int serial) {
		super(Protocol.MAIN_ERRORLOG, Protocol.ERRORLOG_SendLogList, sessionId, serial);
	}

	public SendLogList() {
		super(Protocol.MAIN_ERRORLOG, Protocol.ERRORLOG_SendLogList);
	}

	public String[] getLoglist() {
		return loglist;
	}

	public void setLoglist(String[] loglist) {
		this.loglist = loglist;
	}
}
