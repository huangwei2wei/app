package com.app.handler.account;

import java.util.UUID;

import com.app.dispatch.StatisticsServer;
import com.app.empire.protocol.data.account.GetRoleListOK;
import com.app.empire.protocol.data.account.RoleLogin;
import com.app.net.IConnector;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class GetRoleListOKHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		StatisticsServer.getStatisticsServer().getResNum().getAndIncrement();

		GetRoleListOK ok = (GetRoleListOK) data;
		String[] nicknames = ok.getNickName();
		String str = UUID.randomUUID().toString();
		String nickname = str.replace("-", "").substring(0, 15);
//		if (nicknames.length > 0)
//			nickname = nicknames[0];

		System.out.println("角色：" + nickname);
		IConnector connector = data.getSource();
		RoleLogin roleLogin = new RoleLogin();
		roleLogin.setNickname(nickname);
		roleLogin.setHeroExtId(1);
		roleLogin.setClientModel("--");
		roleLogin.setSystemName("--");
		roleLogin.setSystemVersion("--");
		connector.send(roleLogin);

		return null;
	}
}
