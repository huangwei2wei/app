package com.app.protocol.s2s;

import com.app.protocol.*;

import java.io.IOException;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * 类 <code>S2SDecoder</code>对服务器间的数据进行解码
 * 
 * @see org.apache.mina.filter.codec.ProtocolDecoderAdapter
 * @since JDK 1.6
 */
public class S2SDecoder extends ProtocolDecoderAdapter {
	private Logger log = null;
	protected final AttributeKey CURRENT_DECODER = new AttributeKey(getClass(), "decoder");

	/**
	 * 对服务器间的数据进行解码
	 */
	public S2SDecoder() {
		log = Logger.getLogger(S2SDecoder.class);
	}

	/**
	 * 对服务器间数据进行解码
	 * 
	 * @param session
	 * @param in
	 * @param out
	 */
	@SuppressWarnings("unused")
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// byte remains[] = (byte[]) (byte[]) session.getAttachment();
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
			if (size >= 14) {
				// byte head[] = new byte[4];
				// buffer.get(head);
				// int version = checkVersion(head);
				// if (version == -1) {
				// // session.setAttachment(null);
				// session.setAttribute(CURRENT_DECODER, null);
				// throw new IOException("error protocol 1");
				// }
				int sessionId = buffer.getInt();
				int ser = buffer.getInt();
				int len = buffer.getInt(); // 包长度
				int num = buffer.getShort(); // 包个数
				if (len > 0x19000) {
					// session.setAttachment(null);
					session.setAttribute(CURRENT_DECODER, null);
					throw new IOException("error protocol 2");
				}
				byte minBytes = 8;
				if (len <= size) {
					byte flag = 0;
					INetData datas[] = new INetData[num];
					for (int i = 0; i < num; i++) {
						if (buffer.remaining() < minBytes) {
							// session.setAttachment(null);
							session.setAttribute(CURRENT_DECODER, null);
							throw new IOException("error protocol 3");
						}
						buffer.mark();
						flag = buffer.get();
						byte maintype = buffer.get();// 主协议号
						byte subtype = buffer.get(); // 从协议号
						int dataLen = buffer.getInt();// 数据长度
						int l = buffer.remaining();
						if (dataLen < 0 || dataLen - minBytes >= buffer.remaining()) {
							// session.setAttachment(null);
							session.setAttribute(CURRENT_DECODER, null);
							throw new IOException("error protocol 4");
						}
						byte data[] = new byte[dataLen];
						buffer.reset();
						buffer.get(data);
						datas[i] = new S2SData(data, ser, sessionId, false);
					}
					// buffer.get();
					if ((flag & 1) != 0)
						log.debug((new StringBuilder()).append("***Recv Error Msg:").append(datas[0].getType()).append(".").append(datas[0].getSubType()).toString());
					else
						log.debug((new StringBuilder()).append("***Recv Protocol:").append(datas[0].getType()).append(".").append(datas[0].getSubType()).toString());
					NetPacket packet = new NetPacket();
					packet.datas = datas;
					// session.setAttachment(null);
					session.setAttribute(CURRENT_DECODER, null);
					out.write(packet);
				} else {
					buffer.reset();
					byte bytes[] = new byte[size];
					buffer.get(bytes);
					// session.setAttachment(bytes);
					session.setAttribute(CURRENT_DECODER, bytes);
				}
			} else {
				buffer.reset();
				byte bytes[] = new byte[size];
				buffer.get(bytes);
				// session.setAttachment(bytes);
				session.setAttribute(CURRENT_DECODER, bytes);
			}
		}
	}

	// public int checkVersion(byte head[]) {
	// for (int i = 0; i < INetSegment.HEAD.length - 1; i++) {
	// if (INetSegment.HEAD[i] != head[i]) {
	// return -1;
	// }
	// }
	// return (int) (((head[0] & 0xFF) << 24) | ((head[1] & 0xFF) << 16) | ((head[2] & 0xFF) << 8) | (head[3] & 0xFF));
	// }
}