package com.app.empire.world.server.handler.system;

import com.app.empire.protocol.data.system.GetSystemInfoOk;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class GetSystemInfoHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		GetSystemInfoOk getSystemInfoOk = new GetSystemInfoOk(data.getSessionId(), data.getSerial());
		getSystemInfoOk.setRechargeUrl(ServiceManager.getManager().getConfiguration().getString("rechargeurl"));
		session.write(getSystemInfoOk);
		return null;
	}
}
