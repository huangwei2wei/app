package com.app.empire.world.session;

import org.apache.log4j.Logger;

import com.app.net.ProtocolFactory;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;
import com.app.thread.exec.Action;
import com.app.thread.exec.ActionQueue;

public class HandlerAction extends Action {
	private static Logger	log	= Logger.getLogger(HandlerAction.class);
	private AbstractData	data;

	public HandlerAction(ActionQueue queue, AbstractData data) {
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
				log.error(data + " handler is null,type: " + data.getType() + ",subtype：" + data.getSubType());
				System.err.println(data + " handler is null,type: " + data.getType() + ",subtype：" + data.getSubType());
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

}
