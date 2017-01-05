package com.app.empire.world.server.handler.account;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.account.GetRandomName;
import com.app.empire.protocol.data.account.GetRandomNameOk;
import com.app.empire.world.common.util.Common;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取随机角色名
 * 
 * @author Administrator
 */
public class GetRandomNameHandler implements IDataHandler {
	private Logger log;
	public GetRandomNameHandler() {
		this.log = Logger.getLogger(GetRandomNameHandler.class);
	}
	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		GetRandomName getRandomName = (GetRandomName) data;

		try {
			String name = "nihao";
			GetRandomNameOk getRandomNameOk = new GetRandomNameOk(data.getSessionId(), data.getSerial());
			name = ServiceManager.getManager().getPlayerService().randomName();
			getRandomNameOk.setName(name);
			session.write(  getRandomNameOk);
		} catch (Exception ex) {
			if (null == ex.getMessage() || !ex.getMessage().startsWith(Common.ERRORKEY)) {
				this.log.error(ex, ex);
			}
			if (null != ex.getMessage())
				throw new ProtocolException(ex.getMessage().replace(Common.ERRORKEY, ""), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}