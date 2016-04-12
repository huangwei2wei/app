package com.app.empire.gameaccount.handler.server;

import com.app.empire.gameaccount.session.AcceptSession;
import com.app.empire.protocol.data.server.WorldServerToAccountServer;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class WorldServerToAccountServerHandler implements IDataHandler{
	@Override
	public AbstractData handle(AbstractData data) throws Exception {
		WorldServerToAccountServer handleData = (WorldServerToAccountServer) data;
		AcceptSession session = (AcceptSession)handleData.getHandlerSource();
		session.setWorldServerId(handleData.getWorldServerId());
		System.out.println("设置worldServer标识成功."+handleData.getWorldServerId());
		return null;
	}

}
