package com.app.dispatch;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.app.empire.protocol.Protocol;
import com.app.protocol.INetData;
import com.app.protocol.s2s.S2SSegment;
/**
 * 类 <code>HttpDispatcher</code>继承<code>Dispatcher</code>
 * ,Runnable接口,实现http方式建立连接的分发。
 * 
 * @since JDK 1.6
 */
public class HttpDispatcher implements Dispatcher, Runnable {
	private ControlProcessor processor = null;
	private ChannelService channelService = null;
	private HttpAcceptor acceptor = null;
	private NioSocketConnector connector = null;
	private IoSession serverSession = null;
	private TrustIpService trustIpService = null;
	private Configuration configuration = null;
	private Object lock = new Object();
	public static final String SERVERID = "serverid";
	public static final String SERVERNAME = "servername";
	public static final String SERVERPASSWORD = "serverpassword";
	private static final Logger log = Logger.getLogger(HttpDispatcher.class);
	private boolean connected = false;

	public HttpDispatcher(ControlProcessor processor, Configuration configuration) {
		this.processor = processor;
		this.configuration = configuration;
	}

	public void start() {
		Thread thread = new Thread();
		thread.setName("OnlinePrinter");
		thread.start();
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public void setTrustIpService(TrustIpService trustIpService) {
		this.trustIpService = trustIpService;
	}

	public void dispatchToServer(HttpSession session, Object object) {
		int sessionId = session.getSessionId();
		IoBuffer buffer = (IoBuffer) object;
		buffer.putInt(4, sessionId);
		this.serverSession.write(buffer.duplicate());
	}

	public HttpSession getSession(int sessionId) {
		return this.acceptor.getSession(sessionId);
	}

	public void sendControlSegment(S2SSegment seg) {
		seg.setSessionId(-1);
		this.serverSession.write(IoBuffer.wrap(seg.getPacketByteArray()));
	}

	/**
	 * 连接世界服务器
	 * 
	 * @param address
	 * @param config
	 * @return
	 */
	public ConnectFuture connect(SocketAddress address, int worldreceivebuffsize, int worldwritebuffsize) {
		this.connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
		connector.getSessionConfig().setTcpNoDelay(true);
		connector.getSessionConfig().setReceiveBufferSize(worldreceivebuffsize);
		connector.getSessionConfig().setSendBufferSize(worldwritebuffsize);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerWYDEncoder(), new ServerWYDDecoder()));
		connector.setHandler(new ServerSessionHandler());
		connector.setDefaultRemoteAddress(address);
		ConnectFuture future = this.connector.connect();
		synchronized (this.lock) {
			try {
				this.lock.wait(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!(this.connected)) {
				log.error("世界服务器连接失败!");
			}
		}
		return future;
	}

	public void bind(String address, int port) throws Exception {
		this.acceptor = new HttpAcceptor();
		this.acceptor.bind(address, port, new ClientSessionHandler());
	}

	/**
	 * 将数据包返回手机客户端
	 * 
	 * @param packet
	 */
	public void dispatchToClient(Packet packet) {
		dispatchToClient(packet.sessionId, packet.buffer);
	}

	public void dispatchToClient(int sessionId, IoBuffer buffer) {
		IoSession s = getSession(sessionId);
		if (s != null)
			s.write(buffer);
	}

	protected void unRegisterClient(HttpSession session) {
		this.channelService.removeSessionFromAllChannel(session);
		S2SSegment seg = new S2SSegment(Protocol.MAIN_SERVER, Protocol.SERVER_DispatchLogin);
		seg.writeInt(session.getSessionId());
		sendControlSegment(seg);
	}

	public void unRegisterClient(int sessionId) {
		this.acceptor.closeSession(sessionId);
	}

	protected void processControl(Packet packet) {
		this.processor.process(packet.data);
	}

	public void broadcast(IoBuffer buffer) {
		this.acceptor.broadcast(buffer);
		buffer.clear();
	}

	public void shutdown() {
		this.acceptor.stop();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(60000L);
			} catch (InterruptedException ex) {
			}
			log.info("ONLINE[" + this.acceptor.size() + "]");
		}
	}
	/**
	 * 内部类，处理WordServer返回的数据
	 */
	class ServerSessionHandler extends IoHandlerAdapter {
		public void exceptionCaught(IoSession sesion, Throwable throwable) throws Exception {
			HttpDispatcher.log.error(throwable, throwable);
		}

		public void messageReceived(IoSession session, Object object) throws Exception {
			Packet packet = (Packet) object;
			if (packet.type == Packet.TYPE.BUFFER)
				HttpDispatcher.this.dispatchToClient(packet);
			else
				HttpDispatcher.this.processControl(packet);
		}

		public void sessionClosed(IoSession session) throws Exception {
			serverSession = null;
			connected = false;
		}

		public void sessionCreated(IoSession session) throws Exception {
			serverSession = session;
			S2SSegment seg = new S2SSegment(Protocol.MAIN_SERVER, Protocol.SERVER_DispatchLogin);
			seg.writeString((String) configuration.getProperty("serverid"));
			seg.writeString((String) configuration.getProperty("serverpassword"));
			seg.writeInt(configuration.getInt("maxplayer"));
			sendControlSegment(seg);
			connected = true;
			synchronized (lock) {
				lock.notify();
			}
		}

		public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
		}
	}
	class ClientSessionHandler extends IoHandlerAdapter {
		public void exceptionCaught(IoSession sesion, Throwable throwable) throws Exception {
			throwable.printStackTrace();
		}

		public void messageReceived(IoSession session, Object object) throws Exception {
			HttpDispatcher.this.dispatchToServer((HttpSession) session, object);
		}

		public void sessionClosed(IoSession session) throws Exception {
			HttpDispatcher.this.unRegisterClient((HttpSession) session);
		}

		public void sessionCreated(IoSession session) throws Exception {
			InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
			if (!(HttpDispatcher.this.trustIpService.isTrustIp(address)))
				session.close(true);
		}

		public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
			session.close(true);
		}
	}
	@Override
	public void syncPlayer(INetData data) {
		// TODO Auto-generated method stub
		
	}
}