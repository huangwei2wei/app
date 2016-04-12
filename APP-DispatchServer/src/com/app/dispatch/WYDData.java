package com.app.dispatch;

public class WYDData {
	private byte[] data;
	private short appType;
	private int numOfParameter;
	private int serial;
	private int pos = 0;
	private int sessionId;

	public WYDData(byte[] data, int serial, int sessionId, int version) {
		this.data = data;
		if (version == 1) {
			convertDataToVersion2();
		}
		this.appType = (short) (int) getNumber(this.data, 0, 2);
		this.numOfParameter = this.data[6];
		this.pos = 7;
		this.sessionId = sessionId;
		this.serial = serial;
	}

	public int getSessionId() {
		return this.sessionId;
	}

	public int getSerial() {
		return this.serial;
	}

	public short getAppType() {
		return this.appType;
	}

	public int getNumOfParameter() {
		return this.numOfParameter;
	}

	public boolean readBoolean() throws IllegalAccessException {
		if ((this.pos >= this.data.length - 1) || (this.data[this.pos] != 1)) {
			throw new IllegalAccessException();
		}
		this.pos += 2;
		return ((this.data[(this.pos - 1)] & 0x1) == 1);
	}

	public boolean[] readBooleans() throws IllegalAccessException {
		int len = (int) getNumber(this.data, this.pos, 2);
		this.pos += 2;
		if (this.pos + len > this.data.length) {
			throw new IllegalAccessException();
		}
		boolean[] ret = new boolean[len];
		for (int i = 0; i < len; ++i) {
			ret[i] = (data[pos++] & 1) == 1;
			// ret[i] = (((this.data[(this.pos++)] & 0x1) == 1) ? 1 : false);
		}
		return ret;
	}

	public byte readByte() throws IllegalAccessException {
		if ((this.pos >= this.data.length - 1) || (this.data[this.pos] != 2)) {
			throw new IllegalAccessException();
		}
		this.pos += 2;
		return this.data[(this.pos - 1)];
	}

	public byte[] readBytes() throws IllegalAccessException {
		if (this.data[this.pos] != 18)
			throw new IllegalAccessException();
		this.pos += 1;
		int len = (int) getNumber(this.data, this.pos, 4);
		this.pos += 4;
		if (this.pos + len > this.data.length) {
			throw new IllegalAccessException();
		}
		byte[] ret = new byte[len];
		System.arraycopy(this.data, this.pos, ret, 0, len);
		this.pos += len;
		return ret;
	}

	public short readShort() throws IllegalAccessException {
		if ((this.pos >= this.data.length - 2) || (this.data[this.pos] != 6)) {
			throw new IllegalAccessException();
		}
		this.pos += 3;
		return (short) (int) getNumber(this.data, this.pos - 2, 2);
	}

	public short[] readShorts() throws IllegalAccessException {
		if (this.data[this.pos] != 22)
			throw new IllegalAccessException();
		this.pos += 1;
		int len = (int) getNumber(this.data, this.pos, 2);
		this.pos += 2;
		if (this.pos + len * 2 > this.data.length) {
			throw new IllegalAccessException();
		}
		short[] ret = new short[len];
		for (int i = 0; i < len; ++i) {
			short c = (short) (this.data[(this.pos++)] & 0xFF);
			c = (short) ((c << 8) + (this.data[(this.pos++)] & 0xFF));
			ret[i] = c;
		}
		return ret;
	}

	public int readInt() throws IllegalAccessException {
		if ((this.pos >= this.data.length - 4) || (this.data[this.pos] != 4)) {
			throw new IllegalAccessException();
		}
		this.pos += 5;
		return (int) getNumber(this.data, this.pos - 4, 4);
	}

	public int[] readInts() throws IllegalAccessException {
		if ((this.pos >= this.data.length - 1) || (this.data[this.pos] != 20)) {
			throw new IllegalAccessException();
		}
		this.pos += 1;
		int len = (int) getNumber(this.data, this.pos, 2);
		this.pos += 2;
		if (this.pos + len * 4 > this.data.length) {
			throw new IllegalAccessException();
		}
		int[] ret = new int[len];
		for (int i = 0; i < len; ++i) {
			int c = (char) this.data[(this.pos++)] & 0xFF;
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			ret[i] = c;
		}
		return ret;
	}

	public long readLong() throws IllegalAccessException {
		if ((this.pos >= this.data.length - 8) || (this.data[this.pos] != 5)) {
			throw new IllegalAccessException();
		}
		this.pos += 9;
		return getNumber(this.data, this.pos - 8, 8);
	}

	public long[] readLongs() throws IllegalAccessException {
		if (this.data[this.pos] != 21)
			throw new IllegalAccessException();
		this.pos += 1;
		int len = (int) getNumber(this.data, this.pos, 2);
		this.pos += 2;
		if (this.pos + len * 8 > this.data.length) {
			throw new IllegalAccessException();
		}
		long[] ret = new long[len];
		for (int i = 0; i < len; ++i) {
			long c = (char) this.data[(this.pos++)] & 0xFF;
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			c = (c << 8) + ((char) this.data[(this.pos++)] & 0xFF);
			ret[i] = c;
		}
		return ret;
	}

	public String readString() throws IllegalAccessException {
		byte md = this.data[this.pos];
		if ((this.pos >= this.data.length - 1) || ((md != 7) && (md != 8))) {
			throw new IllegalAccessException();
		}
		this.pos += 1;
		int len = (int) getNumber(this.data, this.pos, 2);
		this.pos += 2;
		if (this.pos + len > this.data.length) {
			throw new IllegalAccessException();
		}
		StringBuffer str = new StringBuffer(len);
		byte[] bytearr = new byte[len];
		int count = 0;
		System.arraycopy(this.data, this.pos, bytearr, 0, len);
		this.pos += len;
		do {
			if (count >= len)
				break;
			int c = bytearr[count] & 0xff;
			switch (c >> 4) {
				case 0 : // '\0'
				case 1 : // '\001'
				case 2 : // '\002'
				case 3 : // '\003'
				case 4 : // '\004'
				case 5 : // '\005'
				case 6 : // '\006'
				case 7 : // '\007'
				{
					count++;
					str.append((char) c);
					break;
				}
				case 12 : // '\f'
				case 13 : // '\r'
				{
					if ((count += 2) > len)
						throw new IllegalAccessException();
					int char2 = bytearr[count - 1];
					if ((char2 & 0xc0) != 128)
						throw new IllegalAccessException();
					str.append((char) ((c & 0x1f) << 6 | char2 & 0x3f));
					break;
				}
				case 14 : // '\016'
				{
					if ((count += 3) > len)
						throw new IllegalAccessException();
					int char2 = bytearr[count - 2];
					int char3 = bytearr[count - 1];
					if ((char2 & 0xc0) != 128 || (char3 & 0xc0) != 128)
						throw new IllegalAccessException();
					str.append((char) ((c & 0xf) << 12 | (char2 & 0x3f) << 6 | (char3 & 0x3f) << 0));
					break;
				}
				case 8 : // '\b'
				case 9 : // '\t'
				case 10 : // '\n'
				case 11 : // '\013'
				default : {
					throw new IllegalAccessException();
				}
			}
		} while (true);
		return new String(str);
	}

	public String[] readStrings() throws IllegalAccessException {
		if (this.data[this.pos] != 23)
			throw new IllegalAccessException();
		this.pos += 1;
		int len1 = (int) getNumber(this.data, this.pos, 2);
		this.pos += 2;
		String[] ret = new String[len1];
		for (int i = 0; i < len1; ++i) {
			int len = (int) getNumber(this.data, this.pos, 2);
			this.pos += 2;
			if (this.pos + len > this.data.length) {
				throw new IllegalAccessException();
			}
			StringBuffer str = new StringBuffer(len);
			byte[] bytearr = new byte[len];
			int count = 0;
			System.arraycopy(this.data, this.pos, bytearr, 0, len);
			this.pos += len;
			do {
				if (count >= len)
					break;
				int c = bytearr[count] & 0xff;
				switch (c >> 4) {
					case 0 : // '\0'
					case 1 : // '\001'
					case 2 : // '\002'
					case 3 : // '\003'
					case 4 : // '\004'
					case 5 : // '\005'
					case 6 : // '\006'
					case 7 : // '\007'
					{
						count++;
						str.append((char) c);
						break;
					}
					case 12 : // '\f'
					case 13 : // '\r'
					{
						if ((count += 2) > len)
							throw new IllegalAccessException();
						int char2 = bytearr[count - 1];
						if ((char2 & 0xc0) != 128)
							throw new IllegalAccessException();
						str.append((char) ((c & 0x1f) << 6 | char2 & 0x3f));
						break;
					}
					case 14 : // '\016'
					{
						if ((count += 3) > len)
							throw new IllegalAccessException();
						int char2 = bytearr[count - 2];
						int char3 = bytearr[count - 1];
						if ((char2 & 0xc0) != 128 || (char3 & 0xc0) != 128)
							throw new IllegalAccessException();
						str.append((char) ((c & 0xf) << 12 | (char2 & 0x3f) << 6 | (char3 & 0x3f) << 0));
						break;
					}
					case 8 : // '\b'
					case 9 : // '\t'
					case 10 : // '\n'
					case 11 : // '\013'
					default : {
						throw new IllegalAccessException();
					}
				}
			} while (true);
			ret[i] = new String(str);
		}
		return ret;
	}

	public static long getNumber(byte[] buf, int off, int len) {
		long l = 0L;
		for (int i = 0; i < len; ++i) {
			l <<= 8;
			l += (buf[(off + i)] & 0xFF);
		}
		return l;
	}

	public byte[] toBytes() {
		return this.data;
	}

	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Type:").append(appType);
		int tmppos = pos;
		pos = 6;
		for (int i = 0; i < numOfParameter; i++) {
			try {
				switch (data[pos]) {
					case 1 : // '\001'
					{
						sbuf.append(", ").append("boolean:").append(readBoolean());
						break;
					}
					case 2 : // '\002'
					{
						sbuf.append(", ").append("byte:").append(readByte());
						break;
					}
					case 4 : // '\004'
					{
						sbuf.append(", ").append("int:").append(readInt());
						break;
					}
					case 5 : // '\005'
					{
						sbuf.append(", ").append("long:").append(readLong());
						break;
					}
					case 6 : // '\006'
					{
						sbuf.append(", ").append("Short:").append(readShort());
						break;
					}
					case 7 : // '\007'
					{
						sbuf.append(", ").append("UTF-8:").append(readString());
						break;
					}
					case 8 : // '\b'
					{
						sbuf.append(", ").append("UTF-16:").append(readString());
						break;
					}
					case 17 : // '\021'
					{
						boolean barr[] = readBooleans();
						sbuf.append(", ").append("boolean array num:").append(barr.length).append(" data:");
						for (int j = 0; j < barr.length; j++)
							sbuf.append(" ").append(barr[j]);
						break;
					}
					case 18 : // '\022'
					{
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
					case 20 : // '\024'
					{
						int barr[] = readInts();
						sbuf.append(", ").append("int array num:").append(barr.length).append(" data:");
						for (int j = 0; j < barr.length; j++)
							sbuf.append(" ").append(barr[j]);
						break;
					}
					case 21 : // '\025'
					{
						long barr[] = readLongs();
						sbuf.append(", ").append("long array num:").append(barr.length).append(" data:");
						for (int j = 0; j < barr.length; j++)
							sbuf.append(" ").append(barr[j]);
						break;
					}
					case 22 : // '\026'
					{
						short barr[] = readShorts();
						sbuf.append(", ").append("short array num:").append(barr.length).append(" data:");
						for (int j = 0; j < barr.length; j++)
							sbuf.append(" ").append(barr[j]);
						break;
					}
					case 23 : // '\027'
					{
						String barr[] = readStrings();
						sbuf.append(", ").append("String array num:").append(barr.length).append(" data:");
						for (int j = 0; j < barr.length; j++)
							sbuf.append(" ").append(barr[j]);
						break;
					}
					case 3 : // '\003'
					case 9 : // '\t'
					case 10 : // '\n'
					case 11 : // '\013'
					case 12 : // '\f'
					case 13 : // '\r'
					case 14 : // '\016'
					case 15 : // '\017'
					case 16 : // '\020'
					case 19 : // '\023'
					default : {
						throw new IllegalAccessException();
					}
				}
				continue;
			} catch (Exception ex) {
				ex.printStackTrace();
				// sbuf.append(", ").append("参数错误num:").append(i).append(" type:"
				// ).append(data[pos]);
			}
			break;
		}
		pos = tmppos;
		return sbuf.toString();
	}

	public void convertDataToVersion2() {
		byte oldType = this.data[0];
		byte[] newData;
		if (oldType == -1) {
			newData = new byte[this.data.length + 2];
			System.arraycopy(this.data, 0, newData, 1, 6);
			newData[0] = -1;
			System.arraycopy(this.data, 6, newData, 8, this.data.length - 6);
			newData[7] = 6;
			newData[8] = 0;
		} else {
			newData = new byte[this.data.length + 1];
			System.arraycopy(this.data, 0, newData, 1, this.data.length);
		}
		this.data = newData;
		WYDSegment.setNumber(this.data.length, this.data, 2, 4);
	}
}