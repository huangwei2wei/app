package com.app.dispatch.data;

import java.io.IOException;
import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.app.protocol.INetData;
import com.app.protocol.s2s.S2SData;

public class ServerWYDDecoder extends ProtocolDecoderAdapter {
	protected final AttributeKey CURRENT_DECODER = new AttributeKey(getClass(), "decoder");

	/** 解码worldServer 发送过来的数据 */
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
		buffer.order(ByteOrder.LITTLE_ENDIAN);// 设置小头在前 默认大头序
		while (buffer.hasRemaining()) {
			buffer.mark();
			int size = buffer.remaining();
			if (size >= 18) {
				// byte[] head = new byte[4];
				// buffer.get(head);
				// int version = compareHead(head);
				// if (version == -1) {
				// session.setAttribute(CURRENT_DECODER, null);
				// throw new IOException("error protocol");
				// }
				int sessionId = buffer.getInt();
				int serial = buffer.getInt();
				int len = buffer.getInt();
				byte target = buffer.get();
				byte proType = buffer.get();
				if (len > 204800) {
					session.setAttribute(CURRENT_DECODER, null);
					throw new IOException("error protocol");
				}
				if (size >= len) {
					if (sessionId == 0) {// 后端处理
						// buffer.mark();
						// byte flag = buffer.get();
						short type = buffer.getShort();
						short subtype = buffer.getShort();
						// int dataLen = buffer.getInt();
						// buffer.reset();
						byte[] data = new byte[len - 18];
						buffer.get(data);
						// buffer.get();
						INetData udata = new S2SData(type, subtype, data, serial, sessionId, target, proType);
						Packet packet = new Packet(udata, type, subtype, target, proType);
						out.write(packet);
					} else {// 发送前端或服务器
						// byte flag = buffer.get();
						short type = buffer.getShort();
						short subtype = buffer.getShort();
						byte[] data = new byte[len];
						buffer.reset();
						buffer.get(data);
						Packet packet = new Packet(IoBuffer.wrap(data), sessionId, type, subtype, target, proType);
						out.write(packet);
					}
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

	// public int compareHead(byte[] head) {
	// for (int i = 0; i < INetSegment.HEAD.length - 1; ++i) {
	// if (INetSegment.HEAD[i] != head[i])
	// return -1;
	// }
	// int version = head[3] - 48;
	// return version;
	// }
}