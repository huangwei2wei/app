package com.app.dispatch.data;

import org.apache.mina.core.buffer.IoBuffer;

import com.app.protocol.INetData;

public class Packet {
	public TYPE type = TYPE.BUFFER;
	public INetData data;
	public IoBuffer buffer;
	public int sessionId = 0;
	public byte pType;
	public byte pSubType;

	public Packet(IoBuffer buffer, int sessionId, byte pType, byte pSubType) {
		this.type = TYPE.BUFFER;
		this.buffer = buffer;
		this.sessionId = sessionId;
		this.pType = pType;
		this.pSubType = pSubType;
	}

	public Packet(INetData data, byte pType, byte pSubType) {
		this.type = TYPE.DATA;
		this.data = data;
		this.pType = pType;
		this.pSubType = pSubType;
	}

	public static enum TYPE {
		BUFFER, DATA;
	}

}