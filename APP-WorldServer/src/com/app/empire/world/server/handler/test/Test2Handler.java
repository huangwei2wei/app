package com.app.empire.world.server.handler.test;

import com.app.empire.protocol.pb.TestMsgProto.TestMsg;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.handler.IDataHandler;

public class Test2Handler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		PbAbstractData test = (PbAbstractData) data;
		TestMsg msg = TestMsg.parseFrom(test.getBytes());
		long a1 = msg.getVipInterimTimeLimit();
		long a2 = msg.getVipTimeLimit();

		System.out.println("a1:" + a1 + "  a2:" + a2);
		return null;
	}
}
