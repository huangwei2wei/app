package com.app.dispatch;
import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.app.protocol.INetSegment;
import com.app.protocol.utils.IpUtil;
public class SimpleWYDDecoderForProxyBind extends ProtocolDecoderAdapter {
	protected final AttributeKey CURRENT_DECODER = new AttributeKey(getClass(), "decoder");

	@SuppressWarnings("unused")
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
			if ((session.getAttribute("SERVERID") == null) && (size >= 8)) {
				short serverNumber = buffer.getShort();
				int ip = buffer.getInt();
				short port = buffer.getShort();
				String serverId = IpUtil.toServerID(ip, port);
				session.setAttribute("SERVERID", serverId);
				session.setAttribute(CURRENT_DECODER, null);
			} else if (size > 18) {
				byte[] head = new byte[4];
				buffer.get(head);
				int version = compareHead(head);
				if (version == -1) {
					session.setAttribute(CURRENT_DECODER, null);
					throw new IOException("error protocol");
				}
				buffer.skip(8);
				int len = buffer.getInt();
				int segNum = buffer.getShort();
				if (segNum != 1) {
					session.setAttribute(CURRENT_DECODER, null);
					throw new IOException("error protocol");
				}
				if ((len > 102400) || (len < 18)) {
					session.setAttribute(CURRENT_DECODER, null);
					throw new IOException("error protocol");
				}
				if (len + 1 <= size) {
					if (buffer.remaining() < 7) {
						session.setAttribute(CURRENT_DECODER, null);
						throw new IOException("error protocol");
					}
					buffer.skip(1);
					buffer.skip(2);
					int dataLen = buffer.getInt();
					if ((dataLen < 0) || (dataLen - 7 >= buffer.remaining())) {
						session.setAttribute(CURRENT_DECODER, null);
						throw new IOException("error protocol");
					}
					byte[] data = new byte[len + 1];
					buffer.reset();
					buffer.get(data);
					out.write(IoBuffer.wrap(data));
					session.setAttribute(CURRENT_DECODER, null);
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
}