package com.app.dispatch;
import java.io.DataOutputStream;
import java.io.OutputStream;
import org.apache.mina.core.buffer.IoBuffer;
public class HttpWYDEncoder {
	public void encode(OutputStream out, IoBuffer[] buffer) throws Exception {
		for (int i = 0; i < buffer.length; ++i) {
			DataOutputStream dos = new DataOutputStream(out);
			int len = buffer[i].remaining();
			byte[] bytes = new byte[len];
			buffer[i].get(bytes);
			dos.write(bytes);
		}
	}
}