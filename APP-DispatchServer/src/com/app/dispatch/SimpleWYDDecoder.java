package com.app.dispatch;
import java.io.IOException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.app.protocol.INetSegment;
public class SimpleWYDDecoder extends CumulativeProtocolDecoder {
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws IOException {
		int start = in.position();
		if (in.hasRemaining()) {
			int size = in.remaining();
			if (size > 19) {
				byte[] head = new byte[4];
				in.get(head);
				if (!(compareHead(head)))
					throw new IOException("error protocol");
				in.skip(8);
				int len = in.getInt();
				if (len > 102400)
					throw new IOException("error protocol");
				if (len + 1 <= size) {
					int limit = in.limit();
					in.position(start);
					in.limit(start + len + 1);
					out.write(in.slice());
					in.position(start + len + 1);
					in.limit(limit);
					return true;
				}
				in.position(start);
				return false;
			}
			return false;
		}
		return false;
	}

	protected boolean compareHead(byte[] value) {
		for (int i = 0; i < INetSegment.HEAD.length; ++i) {
			if (value[i] != INetSegment.HEAD[i])
				return false;
		}
		return true;
	}
}