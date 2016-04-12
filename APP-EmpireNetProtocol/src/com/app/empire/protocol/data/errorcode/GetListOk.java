package com.app.empire.protocol.data.errorcode;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetListOk extends AbstractData {
    private int[] errorCode;
    private String[] errorContent;
	public GetListOk(int sessionId, int serial) {
		super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_GetListOk, sessionId, serial);
	}

	public GetListOk() {
		super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_GetListOk);
	}

    public int[] getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int[] errorCode) {
        this.errorCode = errorCode;
    }

    public String[] getErrorContent() {
        return errorContent;
    }

    public void setErrorContent(String[] errorContent) {
        this.errorContent = errorContent;
    }
}
