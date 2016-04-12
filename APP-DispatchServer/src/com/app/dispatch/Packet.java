package com.app.dispatch;
import org.apache.mina.core.buffer.IoBuffer;

import com.app.protocol.INetData;
public class Packet {
	public TYPE type = TYPE.BUFFER;
	public INetData data;
	public IoBuffer buffer;
	public int sessionId = 0;
	public byte pType;
	public byte pSubType;

	public Packet(IoBuffer buffer, int sessionId) {
		this.type = TYPE.BUFFER;
		this.buffer = buffer;
		this.sessionId = sessionId;
	}

	public Packet(INetData data) {
		this.type = TYPE.UWAPDATA;
		this.data = data;
	}
	public static enum TYPE {
		BUFFER, UWAPDATA;
	}
}