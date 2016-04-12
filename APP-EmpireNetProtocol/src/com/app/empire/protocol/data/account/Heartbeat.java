package com.app.empire.protocol.data.account;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 心跳
 * 
 * @author doter
 * 
 */
public class Heartbeat extends AbstractData {
	private long serviceTime;// 服务器时间

	public Heartbeat(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_Heartbeat, sessionId, serial);
	}
	public Heartbeat() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_Heartbeat);
	}
	public long getServiceTime() {
		return serviceTime;
	}
	public void setServiceTime(long serviceTime) {
		this.serviceTime = serviceTime;
	}

}
