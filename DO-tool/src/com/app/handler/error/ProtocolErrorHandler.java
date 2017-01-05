package com.app.handler.error;

import java.util.UUID;

import com.app.dispatch.StatisticsServer;
import com.app.empire.protocol.data.account.RoleLogin;
import com.app.empire.protocol.data.error.ProtocolError;
import com.app.net.IConnector;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class ProtocolErrorHandler implements IDataHandler {
	public void handle(AbstractData data) throws Exception {
		StatisticsServer.getStatisticsServer().getErrNum().getAndIncrement();
		ProtocolError e = (ProtocolError) data;
////////////////////////////////////////////////////////////
		System.out.println(e.getMsg()+"----------------------------");
		StatisticsServer.getStatisticsServer().getResNum().getAndIncrement();
		IConnector connector = data.getSource();
		String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 15);

//		RoleLogin roleLogin = new RoleLogin();
//		// roleLogin.setNickname("a" + uuid);
//		roleLogin.setNickname("doter");
//		roleLogin.setHeroExtId(1);
//		roleLogin.setClientModel("--");
//		roleLogin.setSystemName("--");
//		roleLogin.setSystemVersion("--");
//		connector.send(roleLogin);

	}
}
