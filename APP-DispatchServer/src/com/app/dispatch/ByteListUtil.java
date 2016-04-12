package com.app.dispatch;

import org.apache.commons.collections.primitives.ByteList;

public class ByteListUtil {
	public ByteListUtil() {
	}

	public static void addBoolean(ByteList list, boolean b) {
		addByte(list, ((byte) (b ? 1 : 0)));
	}

	public static void addBooleans(ByteList list, boolean b[]) {
		for (int i = 0; i < b.length; i++) {
			addBoolean(list, b[i]);
		}
	}

	public static void addByte(ByteList list, byte b) {
		list.add(b);
	}

	public static void addBytes(ByteList list, byte b[]) {
		for (int i = 0; i < b.length; i++) {
			list.add(b[i]);
		}
	}

	public static void addBytes(ByteList list, byte b[], int begin, int len) {
		for (int i = 0; i < len; i++) {
			list.add(b[i + begin]);
		}
	}

	public static void addChar(ByteList list, char value) {
		list.add((byte) (value >> 8 & 0xff));
		list.add((byte) (value & 0xff));
	}

	public static void addChars(ByteList list, char value[]) {
		for (int i = 0; i < value.length; i++) {
			addChar(list, value[i]);
		}
	}

	public static void addShort(ByteList list, short value) {
		list.add((byte) (value >> 8 & 0xff));
		list.add((byte) (value & 0xff));
	}

	public static void addShorts(ByteList list, short value[]) {
		for (int i = 0; i < value.length; i++) {
			addShort(list, value[i]);
		}
	}

	public static void addInt(ByteList list, int value) {
		list.add((byte) (value >> 24 & 0xff));
		list.add((byte) (value >> 16 & 0xff));
		list.add((byte) (value >> 8 & 0xff));
		list.add((byte) (value & 0xff));
	}

	public static void setInt(ByteList list, int pos, int value) {
		list.set(pos++, (byte) (value >> 24 & 0xff));
		list.set(pos++, (byte) (value >> 16 & 0xff));
		list.set(pos++, (byte) (value >> 8 & 0xff));
		list.set(pos, (byte) (value & 0xff));
	}

	public static void addInts(ByteList list, int value[]) {
		for (int i = 0; i < value.length; i++) {
			addInt(list, value[i]);
		}
	}

	public static void addLong(ByteList list, long value) {
		list.add((byte) (int) (value >> 56 & 255L));
		list.add((byte) (int) (value >> 48 & 255L));
		list.add((byte) (int) (value >> 40 & 255L));
		list.add((byte) (int) (value >> 32 & 255L));
		list.add((byte) (int) (value >> 24 & 255L));
		list.add((byte) (int) (value >> 16 & 255L));
		list.add((byte) (int) (value >> 8 & 255L));
		list.add((byte) (int) (value & 255L));
	}

	public static void addLongs(ByteList list, long value[]) {
		for (int i = 0; i < value.length; i++) {
			addLong(list, value[i]);
		}
	}

	public static void addString(ByteList list, String str) {
		int strlen = str.length();
		int utflen = 0;
		char charr[] = new char[strlen];
		str.getChars(0, strlen, charr, 0);
		for (int i = 0; i < strlen; i++) {
			int c = charr[i];
			if (c >= 1 && c <= 127) {
				utflen++;
				continue;
			}
			if (c > 2047) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}
		list.add((byte) (utflen >>> 8 & 0xff));
		list.add((byte) (utflen >>> 0 & 0xff));
		for (int i = 0; i < strlen; i++) {
			int c = charr[i];
			if (c >= 1 && c <= 127) {
				list.add((byte) c);
				continue;
			}
			if (c > 2047) {
				list.add((byte) (0xe0 | c >> 12 & 0xf));
				list.add((byte) (0x80 | c >> 6 & 0x3f));
				list.add((byte) (0x80 | c >> 0 & 0x3f));
			} else {
				list.add((byte) (0xc0 | c >> 6 & 0x1f));
				list.add((byte) (0x80 | c >> 0 & 0x3f));
			}
		}
	}

	public static void addStrings(ByteList list, String str[]) {
		for (int i = 0; i < str.length; i++) {
			addString(list, str[i]);
		}
	}
}