package com.app.protocol.utils;
import org.apache.commons.collections.primitives.ByteList;
public class ByteListUtil {
	public static void addBoolean(ByteList list, boolean b) {
		addByte(list, (b) ? (byte) 1 : (byte) 0);
	}

	public static void addBooleans(ByteList list, boolean[] b) {
		for (int i = 0; i < b.length; ++i)
			addBoolean(list, b[i]);
	}

	public static void addByte(ByteList list, byte b) {
		list.add(b);
	}

	public static void addBytes(ByteList list, byte[] b) {
		for (int i = 0; i < b.length; ++i)
			list.add(b[i]);
	}

	public static void addBytes(ByteList list, byte[] b, int begin, int len) {
		for (int i = 0; i < len; ++i)
			list.add(b[(i + begin)]);
	}

	// public static void addChar(ByteList list, char value) {
	// list.add((byte) (value >> '\b' & 0xFF));
	// list.add((byte) (value & 0xFF));
	// }
	//
	// public static void addChars(ByteList list, char[] value) {
	// for (int i = 0; i < value.length; ++i)
	// addChar(list, value[i]);
	// }

	public static void addShort(ByteList list, short value) {
		// list.add((byte) (value >> 8 & 0xFF));
		// list.add((byte) (value & 0xFF));
		list.add((byte) (value & 0xFF));
		list.add((byte) (value >> 8 & 0xFF));
	}

	public static void addShorts(ByteList list, short[] value) {
		for (int i = 0; i < value.length; ++i)
			addShort(list, value[i]);
	}

	public static void addInt(ByteList list, int value) {
		// list.add((byte) (value >> 24 & 0xFF));
		// list.add((byte) (value >> 16 & 0xFF));
		// list.add((byte) (value >> 8 & 0xFF));
		// list.add((byte) (value & 0xFF));

		list.add((byte) (value & 0xFF));
		list.add((byte) (value >> 8 & 0xFF));
		list.add((byte) (value >> 16 & 0xFF));
		list.add((byte) (value >> 24 & 0xFF));
	}

	public static void setInt(ByteList list, int pos, int value) {
		// list.set(pos++, (byte) (value >> 24 & 0xFF));
		// list.set(pos++, (byte) (value >> 16 & 0xFF));
		// list.set(pos++, (byte) (value >> 8 & 0xFF));
		// list.set(pos, (byte) (value & 0xFF));

		list.set(pos++, (byte) (value & 0xFF));
		list.set(pos++, (byte) (value >> 8 & 0xFF));
		list.set(pos++, (byte) (value >> 16 & 0xFF));
		list.set(pos, (byte) (value >> 24 & 0xFF));
	}

	public static void addInts(ByteList list, int[] value) {
		for (int i = 0; i < value.length; ++i)
			addInt(list, value[i]);
	}

	public static void addLong(ByteList list, long value) {
		// list.add((byte) (int) (value >> 56 & 0xFF));
		// list.add((byte) (int) (value >> 48 & 0xFF));
		// list.add((byte) (int) (value >> 40 & 0xFF));
		// list.add((byte) (int) (value >> 32 & 0xFF));
		// list.add((byte) (int) (value >> 24 & 0xFF));
		// list.add((byte) (int) (value >> 16 & 0xFF));
		// list.add((byte) (int) (value >> 8 & 0xFF));
		// list.add((byte) (int) (value & 0xFF));

		list.add((byte) (int) (value & 0xFF));
		list.add((byte) (int) (value >> 8 & 0xFF));
		list.add((byte) (int) (value >> 16 & 0xFF));
		list.add((byte) (int) (value >> 24 & 0xFF));
		list.add((byte) (int) (value >> 32 & 0xFF));
		list.add((byte) (int) (value >> 40 & 0xFF));
		list.add((byte) (int) (value >> 48 & 0xFF));
		list.add((byte) (int) (value >> 56 & 0xFF));
	}

	public static void addLongs(ByteList list, long[] value) {
		for (int i = 0; i < value.length; ++i)
			addLong(list, value[i]);
	}

	public static void addString(ByteList list, String str) {
		try {
			str = str == null ? "" : str;
			byte[] data = str.getBytes("utf-8");
			// list.add((byte) (data.length >> 8 & 0xFF));
			// list.add((byte) (data.length & 0xFF));
			addShort(list, (short) data.length);
			for (byte d : data) {
				list.add(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addStrings(ByteList list, String[] str) {
		for (int i = 0; i < str.length; ++i)
			addString(list, str[i]);
	}
}
