package com.app.session;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.app.protocol.data.AbstractData;
public class HandlerMonitorService implements Runnable {
	private static final Logger log = Logger.getLogger(HandlerMonitorService.class);
	private static ConcurrentHashMap<AbstractData, Long> msgHandlerMap = new ConcurrentHashMap<AbstractData, Long>();

	public void start() {
		Thread t = new Thread(this);
		t.setName("MonitorService-Thread");
		t.start();
	}

	public static void addMonitor(AbstractData data) {
		msgHandlerMap.put(data, new Long(System.currentTimeMillis()));
	}

	public static void delMonitor(AbstractData data) {
		msgHandlerMap.remove(data);
	}

	public void run() {
		while (true) {
			try {
				long current = System.currentTimeMillis();
				Enumeration<AbstractData> msgs = msgHandlerMap.keys();
				// for (Battle battle : id2battles.values()) {
				AbstractData msg = null;
				while (msgs.hasMoreElements()) {
					msg = msgs.nextElement();
					if (msg != null) {
						Long begTime = msgHandlerMap.get(msg);
						if (current - begTime > 10000) {
							String outinfo = "Msg Moniter : Find Zombie Msg : MainType=" + msg.getType() + ", SubType=" + msg.getSubType()
									+ ",id:" + msg.getSerial();
							log.info(outinfo);
						}
					}
				}
			} catch (Exception e) {
			}
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException ex1) {
			}
		}
	}
}