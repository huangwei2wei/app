package com.app.empire.protocol.data.test;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class Test2 extends AbstractData {
	private byte[] bytes;

	public Test2(int sessionId, int serial) {
		super(Protocol.MAIN_TEST, Protocol.TEST_Test2, sessionId, serial);
	}

	public Test2() {
		super(Protocol.MAIN_TEST, Protocol.TEST_Test2);
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
