package com.app.empire.scene;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.app.empire.protocol.Protocol;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.session.ConnectSession;
import com.app.empire.scene.session.SceneHandler;
import com.app.net.ProtocolFactory;
import com.app.protocol.data.DataBeanFilter;
import com.app.protocol.s2s.S2SDecoder;
import com.app.protocol.s2s.S2SEncoder;
import com.app.session.Session;
import com.app.session.SessionHandler;
import com.app.session.SessionRegistry;

public class SceneServer {
	private static final Logger log = Logger.getLogger(SceneServer.class);

	public static void main(String[] args) {
		new SceneServer().launch();
	}

	private void launch() {
		try {
			ProtocolFactory.init(Protocol.class, "com.app.empire.protocol.data", "com.app.empire.scene.server.handler");
			// ServiceManager serviceManager = ServiceManager.getManager();
			ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
			ServiceManager sm = context.getBean(ServiceManager.class);
			sm.initService();
			ServiceManager.setServiceManager(sm);

			openServerListener();
			System.out.println("场景服务器启动!");
		} catch (Exception e) {
			log.error("server dispatcher launch failed!", e);
			e.printStackTrace();
		}
	}

	/**
	 * 打开服务器监听
	 * 
	 * @throws IOException
	 */
	private void openServerListener() throws IOException {
		// session注册
		SessionRegistry registry = new SessionRegistry();
		SessionHandler sessionHandler = new SceneSessionHandler(registry);

		NioSocketAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		SocketSessionConfig cfg = acceptor.getSessionConfig();
		cfg.setIdleTime(IdleStatus.BOTH_IDLE, 180);
		// cfg.setReuseAddress(true);

		// 添加IoHandler处理线程池
		acceptor.getFilterChain().addFirst("uwap2databean", new DataBeanFilter());
		acceptor.getFilterChain().addFirst("uwap2codec", new ProtocolCodecFilter(new S2SEncoder(), new S2SDecoder()));
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(4, 16));
		// 会话配置
		cfg.setReceiveBufferSize(ServiceManager.getManager().getConfiguration().getInt("receivebuffsize"));
		cfg.setSendBufferSize(ServiceManager.getManager().getConfiguration().getInt("writebuffsize"));
		cfg.setTcpNoDelay(ServiceManager.getManager().getConfiguration().getBoolean("tcpnodelay"));
		acceptor.setHandler(sessionHandler);
		acceptor.setDefaultLocalAddress(new InetSocketAddress(ServiceManager.getManager().getConfiguration()
				.getString("localip"), ServiceManager.getManager().getConfiguration().getInt("port")));
		// 监听
		acceptor.bind();
	}

	/**
	 * 内部类 <code>DispatchSessionHandler</code>ip 分配器相关Handler
	 * 
	 * @see com.app.session.SessionHandler
	 * @since JDK 1.6
	 */
	class SceneSessionHandler extends SceneHandler {
		public SceneSessionHandler(SessionRegistry sessionRegistry) {
			super(sessionRegistry);
		}

		public Session createSession(IoSession session) {
			ConnectSession connSession = new ConnectSession(session);
			return connSession;
		}
	}
}
