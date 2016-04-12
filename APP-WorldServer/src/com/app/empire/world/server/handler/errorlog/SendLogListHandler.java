package com.app.empire.world.server.handler.errorlog;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.errorlog.SendLogList;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取错误列表
 * 
 * @author Administrator
 * 
 */
public class SendLogListHandler implements IDataHandler {
	Logger log = Logger.getLogger(SendLogListHandler.class);

	@SuppressWarnings("unused")
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		SendLogList sendLogList = (SendLogList) data;
		try {

		} catch (Exception ex) {
			log.error(ex, ex);
		}
		return null;
	}
}
