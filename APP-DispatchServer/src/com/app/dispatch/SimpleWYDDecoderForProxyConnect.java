package com.app.dispatch;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.app.protocol.INetSegment;
public class SimpleWYDDecoderForProxyConnect extends ProtocolDecoderAdapter {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SimpleWYDDecoderForProxyConnect.class);
	protected final AttributeKey CURRENT_DECODER = new AttributeKey(getClass(), "decoder");

	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		byte remains[] = (byte[]) (byte[]) session.getAttribute(CURRENT_DECODER);
		IoBuffer buffer = null;
		if (remains != null) {
			buffer = IoBuffer.wrap(remains);
			buffer.setAutoExpand(true);
			buffer.position(remains.length);
			buffer.put(in);
			buffer.flip();
		} else {
			buffer = in;
		}
		while (buffer.hasRemaining()) {
			buffer.mark();
			int size = buffer.remaining();
			if (size > 18) {
				byte[] head = new byte[4];
				buffer.get(head);
				int version = compareHead(head);
				if (version == -1) {
					session.setAttribute(CURRENT_DECODER, null);
					throw new IOException("error protocol");
				}
				int sessionId = buffer.getInt();
				buffer.skip(4);
				int len = buffer.getInt();
				short num = buffer.getShort();
				if (len + 1 <= size) {
					byte[] data = new byte[len + 1];
					buffer.reset();
					buffer.get(data);
					if (num <= 0) {
						Packet1 packet = new Packet1(sessionId);
						packet.param = num;
						out.write(packet);
						session.setAttribute(CURRENT_DECODER, null);
					} else {
						IoBuffer ubuffer = IoBuffer.wrap(data);
						if (check(ubuffer)) {
							Packet1 pakcet = new Packet1(ubuffer, sessionId);
							out.write(pakcet);
						}
						session.setAttribute(CURRENT_DECODER, null);
					}
				} else {
					buffer.reset();
					byte[] bytes = new byte[size];
					buffer.get(bytes);
					session.setAttribute(CURRENT_DECODER, bytes);
				}
			} else {
				buffer.reset();
				byte[] bytes = new byte[size];
				buffer.get(bytes);
				session.setAttribute(CURRENT_DECODER, bytes);
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

	protected boolean check(IoBuffer buffer) {
		byte c = buffer.get(3);
		if (c != INetSegment.HEAD[3])
			return false;
		try {
			int dataLen = buffer.getInt(21);
			int remaining = buffer.remaining();
			if (dataLen != remaining - 19)
				return false;
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
}