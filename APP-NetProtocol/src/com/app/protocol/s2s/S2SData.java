// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space
// Source File Name: SSSData.java
package com.app.protocol.s2s;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.app.protocol.INetData;
import com.app.protocol.data.DataBeanDecoder;

public class S2SData implements INetData {
	private byte data[];
	private byte type;
	private byte subType;
	private int numOfParameter;
	private int serial;
	private int pos;
	private int sessionId;
	private byte flag;
	private boolean sourceCompressed;

	// private static final String sep = ", ";

	public S2SData(byte data[], int serial, int sessionId) {
		this(data, serial, sessionId, false);
	}

	public S2SData(byte data[], int serial, int sessionId, boolean needUncompress) {
		sourceCompressed = false;
		this.data = data;
		flag = (byte) (int) getNumber(data, 0, 1);// 1
		type = (byte) (int) getNumber(data, 1, 1);// 1
		subType = (byte) (int) getNumber(data, 2, 1);// 1
		this.sessionId = sessionId;// 4
		numOfParameter = this.data[7];// 1 字段个数
		this.serial = serial;
		pos = 8;// 从第8个开始读
		sourceCompressed = needUncompress;
	}

	public boolean needCompress() {
		return sourceCompressed;
	}

	public int getSessionId() {
		return sessionId;
	}

	public int getSerial() {
		return serial;
	}

	public byte getType() {
		return type;
	}

	public byte getSubType() {
		return subType;
	}

	public int getNumOfParameter() {
		return numOfParameter;
	}

	public boolean readBoolean() throws IllegalAccessException {
		if (pos + 2 > data.length || data[pos] != 2) {
			throw new IllegalAccessException();
		} else {
			pos += 2;
			return (data[pos - 1] & 1) == 1;
		}
	}

	public boolean[] readBooleans() throws IllegalAccessException {
		if (pos + 3 > data.length || (data[pos] & 0xff) != 130)
			throw new IllegalAccessException();
		pos++;
		int len = (int) getNumber(data, pos, 2);
		pos += 2;
		if (pos + len > data.length)
			throw new IllegalAccessException();
		boolean ret[] = new boolean[len];
		for (int i = 0; i < len; i++)
			ret[i] = (data[pos++] & 1) == 1;
		return ret;
	}

	public byte readByte() throws IllegalAccessException {
		if (pos + 2 > data.length || data[pos] != 1) {
			throw new IllegalAccessException();
		} else {
			pos += 2;
			return data[pos - 1];
		}
	}

	public byte[] readBytes() throws IllegalAccessException {
		if (pos + 5 > data.length || (data[pos] & 0xff) != 129)
			throw new IllegalAccessException();
		pos++;
		int len = (int) getNumber(data, pos, 4);
		pos += 4;
		if (pos + len > data.length) {
			throw new IllegalAccessException();
		} else {
			byte ret[] = new byte[len];
			System.arraycopy(data, pos, ret, 0, len);
			pos += len;
			return ret;
		}
	}

	public short readShort() throws IllegalAccessException {
		if (pos + 3 > data.length || data[pos] != 3) {
			throw new IllegalAccessException();
		} else {
			pos += 3;
			return (short) (int) getNumber(data, pos - 2, 2);
		}
	}

	public short[] readShorts() throws IllegalAccessException {
		if (pos + 3 > data.length || (data[pos] & 0xff) != 131)
			throw new IllegalAccessException();
		pos++;
		int len = (int) getNumber(data, pos, 2);
		pos += 2;
		if (pos + len * 2 > data.length)
			throw new IllegalAccessException();
		short ret[] = new short[len];
		for (int i = 0; i < len; i++) {
			// short c = (short) (data[pos++] & 0xff);
			// c = (short) ((c << 8) + (data[pos++] & 0xff));

			short c = (short) (int) getNumber(data, pos, 2);
			pos += 2;
			ret[i] = c;
		}
		return ret;
	}

	public int readInt() throws IllegalAccessException {
		if (pos + 5 > data.length || data[pos] != 4) {
			throw new IllegalAccessException();
		} else {
			pos += 5;
			return (int) getNumber(data, pos - 4, 4);
		}
	}

	public int[] readInts() throws IllegalAccessException {
		if (pos + 3 > data.length || (data[pos] & 0xff) != 132)
			throw new IllegalAccessException();
		pos++;
		int len = (int) getNumber(data, pos, 2);
		pos += 2;
		if (pos + len * 4 > data.length)
			throw new IllegalAccessException();
		int ret[] = new int[len];
		for (int i = 0; i < len; i++) {
			// int c = (char) data[pos++] & 0xff;
			// c = (c << 8) + ((char) data[pos++] & 0xff);
			// c = (c << 8) + ((char) data[pos++] & 0xff);
			// c = (c << 8) + ((char) data[pos++] & 0xff);
			int c = (int) getNumber(data, pos, 4);
			pos += 4;

			ret[i] = c;
		}
		return ret;
	}

	public long readLong() throws IllegalAccessException {
		if (pos + 9 > data.length || data[pos] != 5) {
			throw new IllegalAccessException();
		} else {
			pos += 9;
			return getNumber(data, pos - 8, 8);
		}
	}

	public long[] readLongs() throws IllegalAccessException {
		if (pos + 3 > data.length || (data[pos] & 0xff) != 133)
			throw new IllegalAccessException();
		pos++;
		int len = (int) getNumber(data, pos, 2);
		pos += 2;
		if (pos + len * 8 > data.length)
			throw new IllegalAccessException();
		long ret[] = new long[len];
		for (int i = 0; i < len; i++) {
			// long c = (char) data[pos++] & 0xff;
			// c = (c << 8) + (long) ((char) data[pos++] & 0xff);
			// c = (c << 8) + (long) ((char) data[pos++] & 0xff);
			// c = (c << 8) + (long) ((char) data[pos++] & 0xff);
			// c = (c << 8) + (long) ((char) data[pos++] & 0xff);
			// c = (c << 8) + (long) ((char) data[pos++] & 0xff);
			// c = (c << 8) + (long) ((char) data[pos++] & 0xff);
			// c = (c << 8) + (long) ((char) data[pos++] & 0xff);

			long c = getNumber(data, pos, 8);
			pos += 8;
			ret[i] = c;
		}
		return ret;
	}

	public String readString() throws IllegalAccessException {
		if (pos + 3 > data.length || data[pos] != 6) {
			throw new IllegalAccessException();
		}
		pos++;
		int len = (int) getNumber(data, pos, 2);
		pos += 2;
		if (pos + len > data.length)
			throw new IllegalAccessException();
		byte bytearr[] = new byte[len];
		System.arraycopy(data, pos, bytearr, 0, len);
		pos += len;
		String ret = null;
		try {
			ret = new String(bytearr, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalAccessException();
		}
		return ret;
	}

	public String[] readStrings() throws IllegalAccessException {
		if (pos + 3 > data.length || (data[pos] & 0xff) != 134)
			throw new IllegalAccessException();
		pos++;
		int len1 = (int) getNumber(data, pos, 2);// 数组长度
		pos += 2;
		String ret[] = new String[len1];
		for (int i = 0; i < len1; i++) {
			int len = (int) getNumber(data, pos, 2);// 字符串长度
			pos += 2;
			if (pos + len > data.length)
				throw new IllegalAccessException();
			byte bytearr[] = new byte[len];
			System.arraycopy(data, pos, bytearr, 0, len);
			pos += len;
			String data = null;
			try {
				data = new String(bytearr, "utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new IllegalAccessException("type:" + type + " subType" + subType + " pos:" + pos);
			}
			ret[i] = data;
		}
		return ret;
	}

	public static long getNumber(byte buf[], int off, int len) {
		long l = 0L;
		// for (int i = 0; i < len; i++) {
		// l <<= 8;
		// l += buf[off + i] & 0xff;
		// }
		for (int i = len - 1; i >= 0; i--) {
			l <<= 8;
			l += buf[off + i] & 0xff;
		}

		return l;
	}

	public byte[] toBytes() {
		return data;
	}

	public List<Object> readList(Field field) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		if (pos + 3 > data.length || (data[pos] & 0xff) != 7)
			throw new IllegalAccessException();
		pos++;
		int len = (int) getNumber(data, pos, 2);
		pos += 2;

		List<Object> list = new ArrayList<Object>();
		Type genericType = field.getGenericType();
		if (!(genericType instanceof ParameterizedType)) // 这样判断genericType 是不是参数化类型。 如Collection<String>就是一个参数化类型
			throw new IllegalAccessException();
		ParameterizedType pt = (ParameterizedType) genericType;
		Type t = pt.getActualTypeArguments()[0];
		Class<?> clazz = (Class<?>) t;
		for (int i = 0; i < len; i++) {
			Object objVo = clazz.newInstance();
			list.add(objVo);
			Field[] fs = objVo.getClass().getDeclaredFields();
			for (Field field2 : fs)
				PropertyUtils.setProperty(objVo, field2.getName(), DataBeanDecoder.getValue(this, field2));
		}
		return list;
	}

	// 读取自定义对象数据类型
	public Object readObj(Field field) throws IllegalAccessException, ClassNotFoundException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		if (pos + 3 > data.length || (data[pos] & 0xff) != 8)
			throw new IllegalAccessException();
		pos++;

		Class<?> clazz = field.getType();
		// Class<?> clazz = Class.forName(field.getType().getName());
		Object objVo = clazz.newInstance();
		Field[] fs = objVo.getClass().getDeclaredFields();
		for (Field field2 : fs)
			PropertyUtils.setProperty(objVo, field2.getName(), DataBeanDecoder.getValue(this, field2));

		return objVo;
	}

	// private String readList() throws IllegalAccessException,
	// InstantiationException, InvocationTargetException, NoSuchMethodException
	// {
	// if (pos + 3 > data.length || (data[pos] & 0xff) != 7)
	// throw new IllegalAccessException();
	// StringBuffer sb = new StringBuffer("List size:");
	// pos++;
	// int len = (int) getNumber(data, pos, 2);
	// pos += 2;
	// sb.append(len);
	// sb.append(",listData:");
	// for (int i = 0; i < len; i++) {
	//
	// }
	// return sb.toString();
	// }

	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Type:").append(type).append(" SubType:").append(subType);
		int tmppos = pos;
		pos = 8;
		for (int i = 0; i < numOfParameter; i++) {
			try {
				switch (data[pos] & 0xff) {
				case 2: // '\002'
				{
					sbuf.append(", ").append("boolean:").append(readBoolean());
					break;
				}
				case 1: // '\001'
				{
					sbuf.append(", ").append("byte:").append(readByte());
					break;
				}
				case 4: // '\004'
				{
					sbuf.append(", ").append("int:").append(readInt());
					break;
				}
				case 5: // '\005'
				{
					sbuf.append(", ").append("long:").append(readLong());
					break;
				}
				case 3: // '\003'
				{
					sbuf.append(", ").append("Short:").append(readShort());
					break;
				}
				case 6: // '\006'
				{
					sbuf.append(", ").append("UTF-8:").append(readString());
					break;
				}
				case 7: // '\007'
				{
					break;
				}
				case 130: {
					boolean barr[] = readBooleans();
					sbuf.append(", ").append("boolean array num:").append(barr.length).append(" data:");
					for (int j = 0; j < barr.length; j++)
						sbuf.append(" ").append(barr[j]);
					break;
				}
				case 129: {
					byte barr[] = readBytes();
					sbuf.append(", ").append("byte array num:").append(barr.length).append(" data:");
					if (barr.length < 40) {
						for (int j = 0; j < barr.length; j++)
							sbuf.append(" ").append(barr[j]);
					} else {
						sbuf.append(" omitted");
					}
					break;
				}
				case 132: {
					int barr[] = readInts();
					sbuf.append(", ").append("int array num:").append(barr.length).append(" data:");
					for (int j = 0; j < barr.length; j++)
						sbuf.append(" ").append(barr[j]);
					break;
				}
				case 133: {
					long barr[] = readLongs();
					sbuf.append(", ").append("long array num:").append(barr.length).append(" data:");
					for (int j = 0; j < barr.length; j++)
						sbuf.append(" ").append(barr[j]);
					break;
				}
				case 131: {
					short barr[] = readShorts();
					sbuf.append(", ").append("short array num:").append(barr.length).append(" data:");
					for (int j = 0; j < barr.length; j++)
						sbuf.append(" ").append(barr[j]);
					break;
				}
				case 134: {
					String barr[] = readStrings();
					sbuf.append(", ").append("String array num:").append(barr.length).append(" data:");
					for (int j = 0; j < barr.length; j++)
						sbuf.append(" ").append(barr[j]);
					break;
				}
				default: {
					return sbuf.toString();
					// throw new IllegalAccessException();
				}
				}
				continue;
			} catch (Exception e) {
				return sbuf.toString();
				// e.printStackTrace();
				// sbuf.append(", ").append("参数错误num:").append(i).append(" type:").append(data[pos]);
			}
			// break;
		}
		pos = tmppos;
		return sbuf.toString();
	}

	public byte getFlag() {
		return flag;
	}
}
