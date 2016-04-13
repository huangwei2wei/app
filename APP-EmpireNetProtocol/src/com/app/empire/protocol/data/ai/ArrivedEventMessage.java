package com.app.empire.protocol.data.ai;

import com.app.empire.protocol.Protocol;

/**
 * 单位到达
 * 
 * @author doter
 * 
 */
public class ArrivedEventMessage extends CommandMessage {
	private long oid;// 到达单位OID的id
	public ArrivedEventMessage(int sessionId, int serial) {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackList, sessionId, serial);
	}
	public ArrivedEventMessage() {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackList);
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}

}
