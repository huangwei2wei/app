package com.app.empire.world.server.handler.test;

import com.app.empire.protocol.data.test.Test2;
import com.app.empire.protocol.pb.test.TestMsgProto.TestMsg;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class Test2Handler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		Test2 test = (Test2) data;
		byte[] b = test.getBytes();
		TestMsg msg = TestMsg.parseFrom(b);
		long a1 = msg.getVipInterimTimeLimit();
		long a2 = msg.getVipTimeLimit();

		System.out.println("a1:" + a1 + "  a2:" + a2);
		return null;
	}
}
