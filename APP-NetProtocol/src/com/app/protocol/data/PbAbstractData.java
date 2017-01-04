package com.app.protocol.data;

public class PbAbstractData extends AbstractData {
	private byte[] bytes;

	/**
	 * 
	 * @param type 协议主类型
	 * @param subType 协议子类型
	 * @param sessionId sessionId
	 * @param serial 系列号
	 * @param target 目的地
	 */
	public PbAbstractData(short type, short subType, int sessionId, int serial, byte target) {
		super(type, subType, sessionId, serial, target);
		setProType(EnumProType.PROBUFFER.getValue());
	}

	/**
	 * @param type 协议主类型
	 * @param subType 协议子类型
	 * @param target 目的地
	 */
	public PbAbstractData(short type, short subType, byte target) {
		super(type, subType, target);
		setProType(EnumProType.PROBUFFER.getValue());
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
