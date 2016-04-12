package com.app.empire.world.server.handler.server;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.server.SetClientIPAddress;
import com.app.empire.world.model.Client;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 设置客户端IP
 * 
 * @author doter
 *
 */
public class SetClientIPAddressHandler implements IDataHandler {
	Logger log;

	public SetClientIPAddressHandler() {
		this.log = Logger.getLogger(SetClientIPAddressHandler.class);
	}

	public AbstractData handle(AbstractData data) throws Exception {
		SetClientIPAddress address = (SetClientIPAddress) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		Client client = session.getAndCreateClient(address.getSession());
		client.setIp(address.getIp());
//		System.out.println("SessionId:"+address.getSession()+"---address:"+address.getIp());
		return null;
	}
}