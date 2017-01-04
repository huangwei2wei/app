package com.app.dispatch.data;

import org.apache.mina.core.buffer.IoBuffer;

import com.app.protocol.INetData;

public class Packet {
	public TYPE		type		= TYPE.BUFFER;
	public INetData	data;
	public IoBuffer	buffer;
	public int		sessionId	= 0;
	public short	pType;
	public short	pSubType;
	public byte		target;
	public byte		proType;

	public Packet(IoBuffer buffer, int sessionId, short pType, short pSubType, byte target, byte proType) {
		this.type = TYPE.BUFFER;
		this.buffer = buffer;
		this.sessionId = sessionId;
		this.pType = pType;
		this.pSubType = pSubType;
		this.target = target;
		this.proType = proType;
	}

	public Packet(INetData data, short pType, short pSubType, byte target, byte proType) {
		this.type = TYPE.DATA;
		this.data = data;
		this.pType = pType;
		this.pSubType = pSubType;
		this.target = target;
		this.proType = proType;
	}

	public static enum TYPE {
		BUFFER, DATA;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public INetData getData() {
		return data;
	}

	public void setData(INetData data) {
		this.data = data;
	}

	public IoBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(IoBuffer buffer) {
		this.buffer = buffer;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public short getpType() {
		return pType;
	}

	public void setpType(short pType) {
		this.pType = pType;
	}

	public short getpSubType() {
		return pSubType;
	}

	public void setpSubType(short pSubType) {
		this.pSubType = pSubType;
	}

	public byte getTarget() {
		return target;
	}

	public byte getProType() {
		return proType;
	}

}