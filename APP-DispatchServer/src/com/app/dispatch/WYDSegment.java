// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space
// Source File Name: UWAPSegment.java
package com.app.dispatch;

import org.apache.commons.collections.primitives.ArrayByteList;
import org.apache.commons.collections.primitives.ByteList;

// Referenced classes of package com.pip.dispatch:
// ByteListUtil, UWAPData, UWAPUtil
public class WYDSegment {
	private ByteList buffer;
	private byte numOfParameter;
	private short type;
	private int serial;
	private int sessionId;

	public WYDSegment(short type, int serial) {
		this(type, serial, -1);
	}

	public WYDSegment(short type, int serial, int sessionId) {
		numOfParameter = 0;
		this.serial = -1;
		this.sessionId = -1;
		this.type = type;
		this.serial = serial;
		this.sessionId = sessionId;
		buffer = new ArrayByteList(128);
		ByteListUtil.addShort(buffer, type);
		ByteListUtil.addInt(buffer, 7);
		ByteListUtil.addByte(buffer, (byte) 0);
	}

	public WYDSegment(short type) {
		this(type, -1);
	}

	public WYDSegment(short type, byte data[], int serial, int sessionId) {
		numOfParameter = 0;
		this.serial = -1;
		this.sessionId = -1;
		this.type = type;
		this.serial = serial;
		this.sessionId = sessionId;
		buffer = new ArrayByteList(data.length);
		ByteListUtil.addBytes(buffer, data);
	}

	public WYDSegment(WYDData data, int sessionId, int playerId) {
		numOfParameter = 0;
		serial = -1;
		this.sessionId = -1;
		type = data.getAppType();
		serial = data.getSerial();
		this.sessionId = sessionId;
		byte bytes[] = data.toBytes();
		buffer = new ArrayByteList(bytes.length + 5);
		ByteListUtil.addShort(buffer, type);
		ByteListUtil.addInt(buffer, bytes.length + 5);
		ByteListUtil.addByte(buffer, (byte) (data.getNumOfParameter() + 1));
		ByteListUtil.addByte(buffer, (byte) 4);
		ByteListUtil.addInt(buffer, playerId);
		ByteListUtil.addBytes(buffer, bytes, 7, bytes.length - 7);
	}

	public WYDSegment(WYDData data, int sessionId) {
		numOfParameter = 0;
		serial = -1;
		this.sessionId = -1;
		type = data.getAppType();
		serial = data.getSerial();
		this.sessionId = sessionId;
		byte bytes[] = data.toBytes();
		buffer = new ArrayByteList(bytes.length);
		ByteListUtil.addBytes(buffer, bytes);
	}

	public short getType() {
		return type;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public byte getNumOfParameter() {
		return numOfParameter;
	}

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	protected void setSize() {
		ByteListUtil.setInt(buffer, 2, buffer.size());
	}

	protected void setNumOfParameter() {
		buffer.set(6, numOfParameter);
	}

	public void write(byte value) {
		ByteListUtil.addByte(buffer, (byte) 2);
		ByteListUtil.addByte(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void write(byte value[]) {
		ByteListUtil.addByte(buffer, (byte) 18);
		ByteListUtil.addInt(buffer, (short) value.length);
		ByteListUtil.addBytes(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeBoolean(boolean value) {
		ByteListUtil.addByte(buffer, (byte) 1);
		ByteListUtil.addByte(buffer, ((byte) (value ? 1 : 0)));
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeBooleans(boolean value[]) {
		ByteListUtil.addByte(buffer, (byte) 17);
		ByteListUtil.addShort(buffer, (short) value.length);
		ByteListUtil.addBooleans(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeShort(short value) {
		ByteListUtil.addByte(buffer, (byte) 6);
		ByteListUtil.addShort(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeShorts(short value[]) {
		ByteListUtil.addByte(buffer, (byte) 22);
		ByteListUtil.addShort(buffer, (short) value.length);
		ByteListUtil.addShorts(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeInt(int value) {
		ByteListUtil.addByte(buffer, (byte) 4);
		ByteListUtil.addInt(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeInts(int value[]) {
		ByteListUtil.addByte(buffer, (byte) 20);
		ByteListUtil.addShort(buffer, (short) value.length);
		ByteListUtil.addInts(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeLong(long value) {
		ByteListUtil.addByte(buffer, (byte) 5);
		ByteListUtil.addLong(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeLongs(long value[]) {
		ByteListUtil.addByte(buffer, (byte) 21);
		ByteListUtil.addShort(buffer, (short) value.length);
		ByteListUtil.addLongs(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeString(String value) {
		ByteListUtil.addByte(buffer, (byte) 7);
		ByteListUtil.addString(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public void writeStrings(String value[]) {
		ByteListUtil.addByte(buffer, (byte) 23);
		ByteListUtil.addShort(buffer, (short) value.length);
		ByteListUtil.addStrings(buffer, value);
		setSize();
		numOfParameter++;
		setNumOfParameter();
	}

	public int size() {
		return buffer.size();
	}

	public byte[] getByteArray() {
		return buffer.toArray();
	}

	public byte[] getPacketByteArray() {
		byte bytes[] = getByteArray();
		ByteList l = new ArrayByteList(bytes.length + 20);
		ByteListUtil.addBytes(l, WYDUtil.HEAD);
		ByteListUtil.addInt(l, sessionId);
		ByteListUtil.addInt(l, serial);
		ByteListUtil.addInt(l, 19 + bytes.length);
		ByteListUtil.addShort(l, (short) 1);
		ByteListUtil.addBytes(l, bytes);
		ByteListUtil.addByte(l, (byte) 0);
		return l.toArray();
	}

	public static void setNumber(int num, byte buf[], int off, int len) {
		for (int i = len - 1; i >= 0; i--) {
			buf[off + i] = (byte) (num & 0xff);
			num >>= 8;
		}
	}
}