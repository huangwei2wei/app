package udp;

import java.net.SocketAddress;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class YourHandler extends IoHandlerAdapter {

	// messageSent是Server响应给Clinet成功后触发的事件
	//
	// @Override
	// public void messageSent(IoSession session, Object message) throws Exception {
	// if (message instanceof IoBuffer) {
	// IoBuffer buffer = (IoBuffer) message;
	// byte[] bb = buffer.array();
	// for (int i = 0; i < bb.length; i++) {
	// System.out.print((char) bb[i]);
	// }
	// }
	// }

	// 抛出异常触发的事件

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
		session.close(true);
	}

	// Server接收到UDP请求触发的事件

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		// System.out.println("messageReceived");
		if (message instanceof IoBuffer) {
			IoBuffer buffer = (IoBuffer) message;
			byte[] bb = buffer.array();
			System.out.println("收到数据长度：" + bb.length);

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < bb.length; i++) {
				sb.append((char) bb[i]);
			}
			System.out.println(sb.toString());

			IoBuffer buffer1 = IoBuffer.wrap("11111".getBytes());// 返回信息给Clinet端
			session.write(buffer1);

			// 声明这里message必须为IoBuffer类型
		}

	}

	// 连接关闭触发的事件

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("Session closed...");
	}

	// 建立连接触发的事件

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("Session created...");
		SocketAddress remoteAddress = session.getRemoteAddress();
		System.out.println(remoteAddress);

	}

	// 会话空闲

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		System.out.println("Session idle...");
		IoBuffer buffer1 = IoBuffer.wrap("aaaaa".getBytes());// 返回信息给Clinet端
		session.write(buffer1);		
	}

	// 打开连接触发的事件，它与sessionCreated的区别在于，一个连接地址（A）第一次请求Server会建立一个Session默认超时时间为1分钟，此时若未达到超时时间这个连接地址（A）再一次向Server发送请求即是sessionOpened（连接地址（A）第一次向Server发送请求或者连接超时后向Server发送请求时会同时触发sessionCreated和sessionOpened两个事件）

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("Session Opened...");
		SocketAddress remoteAddress = session.getRemoteAddress();
		System.out.println(remoteAddress);
	}

}
