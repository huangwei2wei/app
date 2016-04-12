package com.app.handler.account;

import com.app.dispatch.StatisticsServer;
import com.app.empire.protocol.data.account.GetRandomNameOk;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class GetRandomNameOkHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		StatisticsServer.getStatisticsServer().getResNum().getAndIncrement();
		GetRandomNameOk getRandomNameOk = (GetRandomNameOk)data;
		String name = getRandomNameOk.getName();
		
		System.out.println("name: "+name+" sessionId:"+getRandomNameOk.getSessionId());
		return null;
	}

}
