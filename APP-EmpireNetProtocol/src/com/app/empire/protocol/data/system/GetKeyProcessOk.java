package com.app.empire.protocol.data.system;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetKeyProcessOk extends AbstractData {
	private String [] name;
	private String [] value;
	public GetKeyProcessOk(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetKeyProcessOk, sessionId, serial);
    }

    public GetKeyProcessOk() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetKeyProcessOk);
    }

	public String[] getName() {
		return name;
	}

	public void setName(String[] name) {
		this.name = name;
	}

	public String[] getValue() {
		return value;
	}

	public void setValue(String[] value) {
		this.value = value;
	}
}
