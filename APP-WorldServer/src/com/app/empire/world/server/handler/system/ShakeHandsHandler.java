package com.app.empire.world.server.handler.system;

import com.app.empire.protocol.data.system.ShakeHands;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 *  客户端网络握手协议。保持中间环节线路通畅 
 * 
 * @see com.sumsharp.protocol.handler.IDataHandler
 * @author mazheng
 * 
 */
public class ShakeHandsHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		ShakeHands shakehands = (ShakeHands) data;
		if (null != session.getClient(data.getSessionId()) || 1 == shakehands.getCode()) {
			session.write(shakehands);
		}
		return null;
	}
}