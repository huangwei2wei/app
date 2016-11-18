package com.app.protocol.data;

public class PbAbstractData extends AbstractData {
	private byte[] bytes;

	public PbAbstractData(short type, short subType, int sessionId, int serial) {
		super(type, subType, sessionId, serial);
		setProType(EnumProType.PROBUFFER.getValue());
	}

	public PbAbstractData(short type, short subType) {
		super(type, subType);
		setProType(EnumProType.PROBUFFER.getValue());
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
