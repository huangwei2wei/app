package com.app.empire.scene.session;

import org.apache.log4j.Logger;

import com.app.net.ProtocolFactory;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.handler.IDataHandler;
import com.app.thread.exec.Action;
import com.app.thread.exec.ActionQueue;

public class HandlerAction extends Action {
	private static Logger log = Logger.getLogger(HandlerAction.class);
	private PbAbstractData data;

	public HandlerAction(ActionQueue queue, PbAbstractData data) {
		super(queue);
		this.data = data;
	}

	@Override
	public void execute() {
		try {
			IDataHandler handler = ProtocolFactory.getDataHandler(data);
			if (handler != null) {
				long sTime = System.currentTimeMillis();
				handler.handle(data);
				long time = System.currentTimeMillis() - sTime;
				if (time > 50) {
					log.info(handler.getClass().getSimpleName() + "执行 Time:" + time);
				}
			} else {
				log.error(data + " handler is null");
				System.err.println(data + " handler is null");
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

}
