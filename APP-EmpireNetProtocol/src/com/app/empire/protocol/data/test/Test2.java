package com.app.empire.protocol.data.test;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.ProtoBufAbstractData;

public class Test2 extends ProtoBufAbstractData {

	public Test2(int sessionId, int serial) {
		super(Protocol.MAIN_TEST, Protocol.TEST_Test2, sessionId, serial);
	}

	public Test2() {
		super(Protocol.MAIN_TEST, Protocol.TEST_Test2);
	}

}
