package com.app.empire.protocol.data.errorcode;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class CheckOk extends AbstractData {
    private int checkVersion;
	public CheckOk(int sessionId, int serial) {
		super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_CheckOk, sessionId, serial);
	}

	public CheckOk() {
		super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_CheckOk);
	}

    public int getCheckVersion() {
        return checkVersion;
    }

    public void setCheckVersion(int checkVersion) {
        this.checkVersion = checkVersion;
    }
}
