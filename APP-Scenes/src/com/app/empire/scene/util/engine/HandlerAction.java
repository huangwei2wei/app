package com.app.empire.scene.util.engine;

import org.apache.log4j.Logger;

import com.app.net.ProtocolFactory;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;
import com.app.session.Session;

/**
 * 
 * @author doter
 * 
 */
public class HandlerAction extends Action {
	private static Logger log = Logger.getLogger(HandlerAction.class);
	private PbAbstractData data;

	public HandlerAction(PbAbstractData data) {
		this.data = data;
	}

	@Override
	public void execute() {
		Session session = this.data.getHandlerSource();
		try {
			IDataHandler handler = ProtocolFactory.getDataHandler(data);
			if (handler == null) {
				log.info(data + " handler is null");
				System.err.println(data + " handler is null");
				session.handle(data);
			} else {
				long sTime = System.currentTimeMillis();
				handler.handle(data);
				long time = System.currentTimeMillis() - sTime;
				if (time > 50) {
					log.info(handler.getClass().getSimpleName() + "-----Time:" + time);
				}
				// System.out.println("handler:" + handler.getClass().getName() + ",dataobj:" + dataobj.getClass().getSimpleName() + ",sessionId:"
				// + dataobj.getSessionId() + ",useTime:" + time);
			}
		} catch (ProtocolException e) {
			session.sendError(e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("messageReceived-handle-error", e);
		}

	}

}
