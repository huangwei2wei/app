package com.app.empire.gameaccount.stub;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.app.empire.gameaccount.session.AcceptSession;
import com.app.protocol.data.DataBeanFilter;
import com.app.protocol.s2s.S2SDecoder;
import com.app.protocol.s2s.S2SEncoder;
import com.app.session.Session;
import com.app.session.SessionHandler;
import com.app.session.SessionRegistry;
/**
 * 账号服务
 * 
 * @author doter
 *
 */
public class WorldStub {
	private NioSocketAcceptor acceptor;
	private Configuration configuration;
	private int receiveBufferSize = 32767;
	private int sendBufferSize = 32767;
	private static final Logger log = Logger.getLogger(WorldStub.class);
	private SessionRegistry registry;

	public WorldStub(Configuration configuration, SessionRegistry registry) {
		this.registry = registry;
		this.configuration = configuration;
	}

	public void start() throws IOException {
		this.acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		SocketSessionConfig cfg = acceptor.getSessionConfig();
		cfg.setIdleTime(IdleStatus.BOTH_IDLE,180);
		cfg.setTcpNoDelay(true);
		cfg.setReuseAddress(true);
		if (this.configuration.containsKey("receivebuffersize")) {
			this.receiveBufferSize = this.configuration.getInt("receivebuffersize");
		}
		if (this.configuration.containsKey("sendbuffersize")) {
			this.sendBufferSize = this.configuration.getInt("sendbuffersize");
		}
		cfg.setReceiveBufferSize(this.receiveBufferSize);
		cfg.setSendBufferSize(this.sendBufferSize);
		this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new S2SEncoder(), new S2SDecoder()));
		this.acceptor.getFilterChain().addLast("uwap2data", new DataBeanFilter());
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(1, 4));

		// 指定业务逻辑处理器
		acceptor.setHandler(new ClientSessionHandler(this.registry));
		// 设置端口号
		acceptor.setDefaultLocalAddress(new InetSocketAddress(this.configuration.getString("serverip"), this.configuration.getInt("port")));
		acceptor.bind();
		log.info("游戏分区帐号数据服务器启动..."+this.configuration.getString("serverip")+":"+this.configuration.getString("port"));
	}

	class ClientSessionHandler extends SessionHandler {

		public ClientSessionHandler(SessionRegistry registry) {
			super(registry);
		}

		@Override
		public Session createSession(IoSession ioSession) {
//			System.out.println("有 WorldServer 链接过来..");
			return new AcceptSession(ioSession);
		}

		// @Override
		// public void inputClosed(IoSession arg0) throws Exception {
		// // TODO Auto-generated method stub
		//
		// }

	}
}
