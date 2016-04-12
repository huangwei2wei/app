package com.app.dispatch;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.app.empire.protocol.Protocol;
import com.app.protocol.INetData;
import com.app.protocol.INetSegment;
import com.app.protocol.s2s.S2SSegment;
public class SingleSocketDispatcher implements Dispatcher, Runnable {
	private ConcurrentHashMap<Integer, IoSession> sessions = new ConcurrentHashMap<Integer, IoSession>();
	public static final String ATTRIBUTE_PORT = "REMOTE_PROXY_PORT";
	private ControlProcessor processor = null;
	private ChannelService channelService = null;
	private NioSocketAcceptor acceptor = null;
	private NioSocketConnector connector = null;
	private IoSession serverSession = null;
	private IoSession clientSession = null;
	private TrustIpService trustIpService = null;
	private Configuration configuration = null;
	private IpdService ipdService;
	private byte[] lock = new byte[0];
	public static final String SERVERID = "serverid";
	public static final String SERVERNAME = "servername";
	public static final String SERVERPASSWORD = "serverpassword";
	private boolean connected = false;
	private static final Logger log = Logger.getLogger(SingleSocketDispatcher.class);

	public SingleSocketDispatcher(ControlProcessor processor, Configuration configuration) {
		this.processor = processor;
		this.configuration = configuration;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public void setTrustIpService(TrustIpService trustIpService) {
		this.trustIpService = trustIpService;
	}

	public void dispatchToServer(IoSession session, Object object) {
		Packet1 packet = (Packet1) object;
		if (packet.type == Packet1.TYPE.CONTROL) {
			unRegisterClientWithoutNotify(packet.sessionId);
		} else if (!(this.sessions.containsKey(Integer.valueOf(packet.sessionId)))) {
			SingleConnectSession s = new SingleConnectSession(session, packet.sessionId);
			registerClient(s);
			this.serverSession.write(packet.buffer.duplicate());
		} else {
			this.serverSession.write(packet.buffer.duplicate());
		}
	}

	public IoSession getSession(int sessionId) {
		return ((IoSession) this.sessions.get(Integer.valueOf(sessionId)));
	}

	public void sendControlSegment(S2SSegment seg) {
		seg.setSessionId(-1);
		this.serverSession.write(IoBuffer.wrap(seg.getPacketByteArray()));
	}

	public ConnectFuture connect(SocketAddress address, int worldreceivebuffsize, int worldwritebuffsize) {
		this.connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
		connector.getSessionConfig().setTcpNoDelay(true);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerWYDEncoder(), new ServerWYDDecoder()));
		connector.getSessionConfig().setReceiveBufferSize(worldreceivebuffsize);
		connector.getSessionConfig().setSendBufferSize(worldwritebuffsize);
		connector.setHandler(new ServerSessionHandler());
		connector.setDefaultRemoteAddress(address);
		ConnectFuture future = this.connector.connect();
		synchronized (this.lock) {
			try {
				this.lock.wait(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!(this.connected))
				log.error("世界服务器连接失败!");
		}
		return future;
	}

	public void bind(SocketAddress address, int clientreceivebuffsize, int clientwritebuffsize) throws IOException {
		this.acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		this.acceptor.getSessionConfig().setTcpNoDelay(true);
		this.acceptor.getSessionConfig().setReceiveBufferSize(clientreceivebuffsize);
		this.acceptor.getSessionConfig().setSendBufferSize(clientwritebuffsize);
		this.acceptor.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new SimpleWYDEncoder(), new SimpleWYDDecoderForSingleSocket()));
		this.acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(1, 4));
		this.acceptor.setHandler(new ClientSessionHandler());
		this.acceptor.setDefaultLocalAddress(address);
		this.acceptor.bind();
	}

	public void dispatchToClient(Packet packet) {
		dispatchToClient(packet.sessionId, packet.buffer);
	}

	public void dispatchToClient(int sessionId, IoBuffer buffer) {
		IoSession s = (IoSession) this.sessions.get(Integer.valueOf(sessionId));
		if (s != null)
			s.write(buffer);
	}

	public void registerClient(SingleConnectSession session) {
		this.sessions.put(Integer.valueOf(session.getSessionId()), session);
		this.channelService.getWorldChannel().join(session);
	}

	protected void unRegisterClient(SingleConnectSession session) {
		channelService.removeSessionFromAllChannel(session);
		S2SSegment seg = new S2SSegment((byte) Protocol.MAIN_SERVER, (byte) Protocol.SERVER_SessionClosed);
		seg.writeInt(session.getSessionId());
		sendControlSegment(seg);
	}

	private IoBuffer getCloseSegment() {
		IoBuffer buf = IoBuffer.allocate(INetSegment.EMTPY_PACKET.length);
		buf.put(INetSegment.EMTPY_PACKET);
		buf.putShort(16, (short) -1);
		buf.flip();
		return buf;
	}

	private IoBuffer getEmtpySegment(int sessionId) {
		IoBuffer buf = IoBuffer.allocate(INetSegment.EMTPY_PACKET.length);
		buf.put(INetSegment.EMTPY_PACKET);
		buf.putInt(4, sessionId);
		buf.flip();
		return buf;
	}

	public void unRegisterClientWithoutNotify(int sessionId) {
		SingleConnectSession session = (SingleConnectSession) this.sessions.remove(Integer.valueOf(sessionId));
		if (session != null)
			unRegisterClient(session);
	}

	public void unRegisterClient(int sessionId) {
		SingleConnectSession session = (SingleConnectSession) this.sessions.remove(Integer.valueOf(sessionId));
		if (session != null) {
			unRegisterClient(session);
			this.clientSession.write(getEmtpySegment(session.getSessionId()));
		}
	}

	protected void processControl(Packet packet) {
		this.processor.process(packet.data);
	}

	public void broadcast(IoBuffer buffer) {
		for (IoSession session : this.sessions.values()) {
			session.write(buffer.duplicate());
		}
		buffer.clear();
	}

	public void shutdown() {
		this.clientSession.write(getCloseSegment());
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(60000L);
			} catch (InterruptedException ex) {
			}
			log.info("ONLINE[" + this.sessions.size() + "]");
		}
	}

	public void setIpdService(IpdService ipdService) {
		this.ipdService = ipdService;
	}
	class ServerSessionHandler extends IoHandlerAdapter {
		public void exceptionCaught(IoSession sesion, Throwable throwable) throws Exception {
			SingleSocketDispatcher.log.error(throwable, throwable);
		}

		public void messageReceived(IoSession session, Object object) throws Exception {
			Packet packet = (Packet) object;
			if (packet.type == Packet.TYPE.BUFFER)
				SingleSocketDispatcher.this.dispatchToClient(packet);
			else
				SingleSocketDispatcher.this.processControl(packet);
		}

		public void sessionClosed(IoSession session) throws Exception {
		}

		public void sessionCreated(IoSession session) throws Exception {
			serverSession = session;
			S2SSegment seg = new S2SSegment(Protocol.MAIN_SERVER, Protocol.SERVER_DispatchLogin);
			seg.writeString((String) SingleSocketDispatcher.this.configuration.getProperty("serverid"));
			seg.writeString((String) SingleSocketDispatcher.this.configuration.getProperty("serverpassword"));
			seg.writeInt(SingleSocketDispatcher.this.configuration.getInt("maxplayer"));
			SingleSocketDispatcher.this.sendControlSegment(seg);
			connected = true;
			synchronized (lock) {
				SingleSocketDispatcher.this.lock.notify();
			}
		}

		public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
		}
	}
	class ClientSessionHandler extends IoHandlerAdapter {
		public void exceptionCaught(IoSession sesion, Throwable throwable) throws Exception {
			SingleSocketDispatcher.log.error(throwable, throwable);
		}

		public void messageReceived(IoSession session, Object object) throws Exception {
			if (object instanceof Short) {
				if (SingleSocketDispatcher.this.configuration.getProperty("proxyport") == null)
					SingleSocketDispatcher.this.configuration.addProperty("proxyport", (Short) object);
				System.out.println("------------------");
				SingleSocketDispatcher.this.ipdService.ipdConnect();
			} else {
				SingleSocketDispatcher.this.dispatchToServer(session, object);
			}
		}

		public void sessionClosed(IoSession session) throws Exception {
			SingleSocketDispatcher.log.info("SocketProxy Closed");
			synchronized (sessions) {
				for (IoSession s : SingleSocketDispatcher.this.sessions.values()) {
					SingleSocketDispatcher.this.unRegisterClient((SingleConnectSession) s);
				}
				SingleSocketDispatcher.this.sessions.clear();
				clientSession = null;
			}
		}

		public void sessionCreated(IoSession session) throws Exception {
			synchronized (sessions) {
				InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
				if (!(SingleSocketDispatcher.this.trustIpService.isTrustIp(address))) {
					session.close(true);
				} else {
					SingleSocketDispatcher.log.info("SocketProxy Connected");
					clientSession = session;
					SingleSocketDispatcher.this.configuration.addProperty("proxyip", address.getAddress().getHostAddress());
				}
			}
		}

		public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
		}
	}
	@Override
	public void syncPlayer(INetData data) {
		// TODO Auto-generated method stub
		
	}
}