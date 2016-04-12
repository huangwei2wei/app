package com.app.dispatch;

import org.apache.mina.core.buffer.IoBuffer;

public class Packet1 {
	public TYPE type = TYPE.BUFFER;
	public IoBuffer buffer;
	public int sessionId = 0;
	public int param = 0;

	public Packet1(IoBuffer buffer, int sessionId) {
		this.type = TYPE.BUFFER;
		this.buffer = buffer;
		this.sessionId = sessionId;
	}

	public Packet1(int sessionId) {
		this.type = TYPE.CONTROL;
		this.sessionId = sessionId;
	}
	public static enum TYPE {
		BUFFER, CONTROL;
	}
}