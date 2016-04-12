package com.app.empire.world.server.handler.purchase;

import org.apache.log4j.Logger;

import com.app.empire.world.service.factory.ServiceManager;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取兑换比率列表
 * 
 * @author Administrator
 */
public class RequestSmsCodeSerialidHandler implements IDataHandler {
	Logger log = Logger.getLogger(RequestSmsCodeSerialidHandler.class);

	public AbstractData handle(AbstractData data) throws Exception {
		ServiceManager.getManager().getOrderSerialService().addSerialInfo(data);
		return null;
	}
}
