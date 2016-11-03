package com.app.dispatch.data;

import java.io.IOException;
import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class ClientDecoder extends ProtocolDecoderAdapter {
	protected final AttributeKey CURRENT_DECODER = new AttributeKey(getClass(), "decoder");
	public static int companyCode = -1;
	public static int machineCode = -1;

	/** 解码玩家发送过来的数据 */
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		byte remains[] = (byte[]) session.getAttribute(CURRENT_DECODER);
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
			if (size > 13) {
				// byte[] head = new byte[4];
				// buffer.get(head);
				// int version = compareHead(head);// 验证版本
				// if (version == -1) {
				// session.setAttribute(CURRENT_DECODER, null);
				// System.out.println("error protocol_1");
				// throw new IOException("error protocol_1");
				// }
				buffer.skip(8);
				int len = buffer.getInt();// 包长度
				byte segNum = buffer.get();// 包个数
				if (segNum < 1) {
					session.setAttribute(CURRENT_DECODER, null);
					System.out.println("error protocol_2");
					throw new IOException("error protocol_2");
				}
				if ((len > 102400) || (len < 18)) {
					session.setAttribute(CURRENT_DECODER, null);
					System.out.println("error protocol_3");
					throw new IOException("error protocol_3");
				}
				if (size >= len) {// 满足一个包的数据
					buffer.reset();
					buffer.skip(13);
					if (buffer.remaining() < 7) {
						session.setAttribute(CURRENT_DECODER, null);
						System.out.println("error protocol_4");
						throw new IOException("error protocol_4");
					}
					buffer.skip(2);
					int dataLen = buffer.getInt();// 数据长度
					if ((dataLen < 0) || (dataLen - 7 >= buffer.remaining())) {
						session.setAttribute(CURRENT_DECODER, null);
						System.out.println("error protocol_5");
						throw new IOException("error protocol_5");
					}

					byte[] data = new byte[len];
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
			} else if (size > 0) {
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