package com.app.empire.world.server.handler.error;

import com.app.empire.protocol.data.error.ProtocolError;
import com.app.empire.world.exception.ErrorMessages;
import com.app.empire.world.request.SessionRequest;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.net.IRequest;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class ProtocolErrorHandler implements IDataHandler {
	public AbstractData handle(AbstractData message) throws Exception {
		ProtocolError msg = (ProtocolError) message;
		IRequest request = ServiceManager.getManager().getRequestService().remove(msg.getSerial());
		if (request == null) {
			return null;
		}
		ConnectSession session = ((SessionRequest) request).getConnectionSession();
		msg.setSerial(request.getId());
		msg.setMsg(ErrorMessages.getErrorMesssage(msg.getCode()));
		msg.setSessionId(((SessionRequest) request).getSessionId());
		if (session != null)
			session.write(msg);
		return null;
	}
}