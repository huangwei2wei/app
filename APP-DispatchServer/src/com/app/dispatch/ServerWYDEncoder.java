package com.app.dispatch;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
public class ServerWYDEncoder implements ProtocolEncoder {
	public void dispose(IoSession ioSession) throws Exception {
	}

	public void encode(IoSession session, Object object, ProtocolEncoderOutput out) throws Exception {
		out.write((IoBuffer) object);
	}
}