package com.app.empire.world.server.handler.purchase;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.purchase.GetCallBackUriOk;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取充值验证回调地址和端口
 * 
 * @author Administrator
 */
public class GetCallBackUriHandler implements IDataHandler {
	Logger log = Logger.getLogger(GetCallBackUriHandler.class);

	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		try {
			GetCallBackUriOk getCallBackUriOk = new GetCallBackUriOk(data.getSessionId(), data.getSerial());
			getCallBackUriOk.setIp(ServiceManager.getManager().getConfiguration().getString("callbackip"));
			// getCallBackUriOk.setIp("183.63.8.194");
			getCallBackUriOk.setPort(ServiceManager.getManager().getConfiguration().getString("httpPort"));
			session.write(getCallBackUriOk);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
