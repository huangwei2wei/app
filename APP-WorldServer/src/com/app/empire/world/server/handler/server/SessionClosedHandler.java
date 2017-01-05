package com.app.empire.world.server.handler.server;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.server.PlayerLogout;
import com.app.empire.protocol.data.server.SessionClosed;
import com.app.empire.world.model.Client;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 用户下线
 */
public class SessionClosedHandler implements IDataHandler {
	Logger log = Logger.getLogger(SessionClosedHandler.class);

	public void handle(AbstractData data) throws Exception {
		SessionClosed closed = (SessionClosed) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		Client client = session.getClient(closed.getSession());
		if (client != null) {
			log.info("SessionClosed SessionId:" + client.getSessionId() + "----playerId:" + client.getPlayerId());
			session.removeClient(client);
			// 发送至账号服务器
			PlayerLogout playerLogout = new PlayerLogout();
			playerLogout.setAccountId(client.getAccountId());
			playerLogout.setLevel(0);
			playerLogout.setKey("");
			ServiceManager.getManager().getAccountSkeleton().send(playerLogout);
			// ServiceManager.getManager().getAbstractService().removeAllAbstractInfoById(session.getSessionId()
			// + "-" + client.getSessionId());
		} else {
			log.info("注意：用户下线异常　sessionId: " + closed.getSession());
		}
	}
}