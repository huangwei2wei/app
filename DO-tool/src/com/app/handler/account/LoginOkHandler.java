package com.app.handler.account;

import com.app.dispatch.StatisticsServer;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 
 */
public class LoginOkHandler implements IDataHandler {
	public void handle(AbstractData data) throws Exception {
		System.out.println("账号登录数量:" + StatisticsServer.getStatisticsServer().getAccountNum().incrementAndGet());
		StatisticsServer.getStatisticsServer().getResNum().getAndIncrement();
	}
}