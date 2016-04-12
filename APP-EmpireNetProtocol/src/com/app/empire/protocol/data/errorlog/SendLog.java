package com.app.empire.protocol.data.errorlog;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

 
public class SendLog extends AbstractData {
    private String logfilename;
    private String[] logs;
	public SendLog(int sessionId, int serial) {
		super(Protocol.MAIN_ERRORLOG, Protocol.ERRORLOG_SendLogList, sessionId, serial);
	}

	public SendLog() {
		super(Protocol.MAIN_ERRORLOG, Protocol.ERRORLOG_SendLogList);
	}

    public String getLogfilename() {
        return logfilename;
    }

    public void setLogfilename(String logfilename) {
        this.logfilename = logfilename;
    }

    public String[] getLogs() {
        return logs;
    }

    public void setLogs(String[] logs) {
        this.logs = logs;
    }
}
