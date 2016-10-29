package com.app.protocol.s2s;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.primitives.ArrayByteList;
import org.apache.commons.collections.primitives.ByteList;

import com.app.protocol.INetData;
import com.app.protocol.INetSegment;
import com.app.protocol.data.DataBeanEncoder;
import com.app.protocol.utils.ByteListUtil;

public class S2SSegment implements INetSegment {
	private ByteList buffer;
	private byte numOfParameter;
	private byte type;
	private byte subType;
	private int serial;
	private int sessionId;
	private byte flag;

	public S2SSegment(byte type, byte subType, int serial, byte flag) {
		this(type, subType, serial, -1, flag);
	}

	public S2SSegment(byte type, byte subType, int serial, int sessionId, byte flag) {
		this.numOfParameter = 0;
		this.serial = -1;
		this.type = type;
		this.subType = subType;
		this.serial = serial;
		this.sessionId = sessionId;
		this.flag = flag;
		this.buffer = new ArrayByteList(128);
		ByteListUtil.addByte(this.buffer, flag);// 1
		ByteListUtil.addByte(this.buffer, type);// 1
		ByteListUtil.addByte(this.buffer, subType);// 1
		ByteListUtil.addInt(this.buffer, 8);// 4
		ByteListUtil.addByte(this.buffer, (byte) 0);// 1
	}

	public S2SSegment(byte type, byte subType, byte flag) {
		this(type, subType, -1, flag);
	}

	public S2SSegment(byte type, byte subType) {
		this(type, subType, (byte) -1, (byte) 0);
	}

	public S2SSegment(byte type, byte subType, byte[] data, int serial, int sessionId, byte flag) {
		this.numOfParameter = 0;
		this.serial = -1;
		this.sessionId = -1;
		this.flag = 0;
		this.type = type;
		this.subType = subType;
		this.serial = serial;
		this.sessionId = sessionId;
		this.flag = flag;
		this.buffer = new ArrayByteList(data.length);
		ByteListUtil.addBytes(this.buffer, data);
	}

	public S2SSegment(INetData data, int sessionId) {
		this.numOfParameter = 0;
		this.serial = -1;
		this.sessionId = -1;
		this.flag = 0;
		this.type = data.getType();
		this.subType = data.getSubType();
		this.serial = data.getSerial();
		this.sessionId = sessionId;
		this.flag = data.getFlag();
		byte[] bytes = data.toBytes();
		this.buffer = new ArrayByteList(bytes.length);
		ByteListUtil.addBytes(this.buffer, bytes);
	}

	public byte getType() {
		return this.type;
	}

	public byte getSubType() {
		return this.subType;
	}

	public int getSessionId() {
		return this.sessionId;
	}

	public byte getFlag() {
		return this.flag;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public byte getNumOfParameter() {
		return this.numOfParameter;
	}

	public int getSerial() {
		return this.serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	protected void setSize() {
		ByteListUtil.setInt(this.buffer, 3, this.buffer.size());
	}

	protected void setNumOfParameter() {
		this.buffer.set(7, this.numOfParameter);
	}

	public void write(byte value) {
		ByteListUtil.addByte(this.buffer, (byte) 1);
		ByteListUtil.addByte(this.buffer, value);
		setSize();
		S2SSegment tmp21_20 = this;
		tmp21_20.numOfParameter = (byte) (tmp21_20.numOfParameter + 1);
		setNumOfParameter();
	}

	public void write(byte[] value) {
		ByteListUtil.addByte(this.buffer, (byte) -127);
		ByteListUtil.addInt(this.buffer, value.length);
		ByteListUtil.addBytes(this.buffer, value);
		setSize();
		S2SSegment tmp31_30 = this;
		tmp31_30.numOfParameter = (byte) (tmp31_30.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeBoolean(boolean value) {
		ByteListUtil.addByte(this.buffer, (byte) 2);
		ByteListUtil.addByte(this.buffer, (value) ? (byte) 1 : (byte) 0);
		setSize();
		S2SSegment tmp29_28 = this;
		tmp29_28.numOfParameter = (byte) (tmp29_28.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeBooleans(boolean[] value) {
		ByteListUtil.addByte(this.buffer, (byte) -126);
		ByteListUtil.addShort(this.buffer, (short) value.length);
		ByteListUtil.addBooleans(this.buffer, value);
		setSize();
		S2SSegment tmp32_31 = this;
		tmp32_31.numOfParameter = (byte) (tmp32_31.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeShort(short value) {
		ByteListUtil.addByte(this.buffer, (byte) 3);
		ByteListUtil.addShort(this.buffer, value);
		setSize();
		S2SSegment tmp21_20 = this;
		tmp21_20.numOfParameter = (byte) (tmp21_20.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeShorts(short[] value) {
		ByteListUtil.addByte(this.buffer, (byte) -125);
		ByteListUtil.addShort(this.buffer, (short) value.length);
		ByteListUtil.addShorts(this.buffer, value);
		setSize();
		S2SSegment tmp32_31 = this;
		tmp32_31.numOfParameter = (byte) (tmp32_31.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeInt(int value) {
		ByteListUtil.addByte(this.buffer, (byte) 4);
		ByteListUtil.addInt(this.buffer, value);
		setSize();
		S2SSegment tmp21_20 = this;
		tmp21_20.numOfParameter = (byte) (tmp21_20.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeInts(int[] value) {
		ByteListUtil.addByte(this.buffer, (byte) -124);
		ByteListUtil.addShort(this.buffer, (short) value.length);
		ByteListUtil.addInts(this.buffer, value);
		setSize();
		S2SSegment tmp32_31 = this;
		tmp32_31.numOfParameter = (byte) (tmp32_31.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeLong(long value) {
		ByteListUtil.addByte(this.buffer, (byte) 5);
		ByteListUtil.addLong(this.buffer, value);
		setSize();
		S2SSegment tmp21_20 = this;
		tmp21_20.numOfParameter = (byte) (tmp21_20.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeLongs(long[] value) {
		ByteListUtil.addByte(this.buffer, (byte) -123);
		ByteListUtil.addShort(this.buffer, (short) value.length);
		ByteListUtil.addLongs(this.buffer, value);
		setSize();
		S2SSegment tmp32_31 = this;
		tmp32_31.numOfParameter = (byte) (tmp32_31.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeString(String value) {
		ByteListUtil.addByte(this.buffer, (byte) 6);
		ByteListUtil.addString(this.buffer, value);
		setSize();
		S2SSegment tmp22_21 = this;
		tmp22_21.numOfParameter = (byte) (tmp22_21.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeStrings(String[] value) {
		ByteListUtil.addByte(this.buffer, (byte) -122);
		ByteListUtil.addShort(this.buffer, (short) value.length);
		ByteListUtil.addStrings(this.buffer, value);
		setSize();
		S2SSegment tmp32_31 = this;
		tmp32_31.numOfParameter = (byte) (tmp32_31.numOfParameter + 1);
		setNumOfParameter();
	}

	public void writeList(List<Object> list) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ByteListUtil.addByte(this.buffer, (byte) 7);
		ByteListUtil.addShort(this.buffer, (short) list.size());
		for (Object obj : list) {
			Field[] fs = obj.getClass().getDeclaredFields();
			for (Field f : fs)
				DataBeanEncoder.setValue(this, f, PropertyUtils.getProperty(obj, f.getName()));
		}
		setSize();
		S2SSegment tmp22_21 = this;
		tmp22_21.numOfParameter = (byte) (tmp22_21.numOfParameter + 1);
		setNumOfParameter();
		// setNumOfParameter();
	}

	@Override
	public void writeObj(Object obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ByteListUtil.addByte(this.buffer, (byte) 8);

		Field[] fs = obj.getClass().getDeclaredFields();
		for (Field f : fs)
			DataBeanEncoder.setValue(this, f, PropertyUtils.getProperty(obj, f.getName()));

		setSize();
		S2SSegment tmp22_21 = this;
		tmp22_21.numOfParameter = (byte) (tmp22_21.numOfParameter + 1);
		setNumOfParameter();
	}

	public int size() {
		return this.buffer.size();
	}

	public byte[] getByteArray() {
		return this.buffer.toArray();
	}

	public byte[] getPacketByteArray() {
		byte[] bytes = getByteArray();
		ByteList byteList = new ArrayByteList(bytes.length + 20);
		//ByteListUtil.addBytes(byteList, INetSegment.HEAD);// 4
		ByteListUtil.addInt(byteList, this.sessionId);// 4
		ByteListUtil.addInt(byteList, this.serial);// 4
		ByteListUtil.addInt(byteList, 14 + bytes.length);// 4
		ByteListUtil.addShort(byteList, (short) 1);// 2
		ByteListUtil.addBytes(byteList, bytes);
		//ByteListUtil.addByte(byteList, (byte) 0);// 1
		return byteList.toArray();
	}

	public static void setNumber(int num, byte[] buf, int off, int len) {
		for (int i = len - 1; i >= 0; --i) {
			buf[(off + i)] = (byte) (num & 0xFF);
			num >>= 8;
		}
	}
}
