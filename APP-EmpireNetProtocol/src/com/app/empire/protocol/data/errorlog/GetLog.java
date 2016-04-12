package com.app.empire.protocol.data.errorlog;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;


public class GetLog extends AbstractData {
    private String logfilename;
	public GetLog(int sessionId, int serial) {
		super(Protocol.MAIN_ERRORLOG, Protocol.ERRORLOG_GetLog, sessionId, serial);
	}

	public GetLog() {
		super(Protocol.MAIN_ERRORLOG, Protocol.ERRORLOG_GetLog);
	}

    public String getLogfilename() {
        return logfilename;
    }

    public void setLogfilename(String logfilename) {
        this.logfilename = logfilename;
    }
}
