package com.app.dispatch;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import com.app.protocol.INetSegment;

public class HttpWYDDecoder {
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Packet[] decode(InputStream in) throws Exception {
		List l = new LinkedList();
		Packet[] ret;
		while (true) {
			try {
				byte[] head = new byte[19];
				if (readFull(in, head) != head.length) {
					return null;
				}
				int version = compareHead(head);
				if (version == -1) {
					throw new IOException("error protocol");
				}
				int sessionId = (int) getNumber(head, 4, 4);
				int len = (int) getNumber(head, 12, 4);
				len -= head.length;
				byte[] buf = null;
				if (len > 0) {
					buf = new byte[len + 1];
					if (readFull(in, buf) != len + 1) {
						throw new IOException("No enouth input");
					}
				}
				byte[] bytes = new byte[len + 20];
				System.arraycopy(head, 0, bytes, 0, 19);
				if (buf != null) {
					System.arraycopy(buf, 0, bytes, 19, buf.length);
					IoBuffer buffer = IoBuffer.wrap(bytes);
					Packet packet = new Packet(buffer, sessionId);
					packet.pType = buf[0];
					packet.pSubType = buf[1];
					System.out.println("Data Type： 0x" + Integer.toHexString(packet.pType) + ".0x" + Integer.toHexString(packet.pSubType));
					l.add(packet);
				} else {
					ret = new Packet[l.size()];
					l.toArray(ret);
					return ret;
				}
			} catch (IOException e) {
				ret = new Packet[l.size()];
				l.toArray(ret);
				return ret;
			}
		}
	}

	public int compareHead(byte[] head) {
		for (int i = 0; i < INetSegment.HEAD.length - 1; ++i) {
			if (INetSegment.HEAD[i] != head[i])
				return -1;
		}
		int version = head[3] - 48;
		return version;
	}

	public static long getNumber(byte[] buf, int off, int len) {
		long l = 0L;
		for (int i = 0; i < len; ++i) {
			l <<= 8;
			l += (buf[(off + i)] & 0xFF);
		}
		return l;
	}

	protected static int readFull(InputStream in, byte[] buf) throws IOException {
		int len = 0;
		try {
			while (len < buf.length) {
				int l = in.read(buf, len, buf.length - len);
				if (l < 0) {
					throw new EOFException();
				}
				len += l;
			}
		} finally {
			for (int i = len; i < buf.length; ++i) {
				buf[i] = 0;
			}
		}
		return len;
	}
}