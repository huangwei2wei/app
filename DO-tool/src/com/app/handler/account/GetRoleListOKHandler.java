package com.app.handler.account;

import java.util.List;
import java.util.UUID;

import com.app.dispatch.StatisticsServer;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg;
import com.app.empire.protocol.pb.account.RoleLoginMsgProto.RoleLoginMsg;
import com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg;
import com.app.net.IConnector;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.handler.IDataHandler;

public class GetRoleListOKHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		StatisticsServer.getStatisticsServer().getResNum().getAndIncrement();

		// GetRoleListOK ok = (GetRoleListOK) data;
		PbAbstractData msg = (PbAbstractData) data;
		RoleListMsg ok = RoleListMsg.parseFrom(msg.getBytes());

		List<RoleMsg> roleList = ok.getRoleList();

		String str = UUID.randomUUID().toString();
		String nickname = str.replace("-", "").substring(0, 15);
		for (RoleMsg roleMsg : roleList) {
			nickname = roleMsg.getNickName();
		}

		System.out.println("角色：" + nickname);
		IConnector connector = data.getSource();
		RoleLoginMsg.Builder roleLogin = RoleLoginMsg.newBuilder();

		// RoleLogin roleLogin = new RoleLogin();
		roleLogin.setNickname(nickname);
		roleLogin.setHeroExtId(1);
		roleLogin.setClientModel("--");
		roleLogin.setSystemName("--");
		roleLogin.setSystemVersion("--");
		connector.send(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RoleLogin, roleLogin.build(), EnumTarget.CLIENT.getValue());

		return null;
	}
}
