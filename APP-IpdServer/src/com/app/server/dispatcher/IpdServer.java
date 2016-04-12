package com.app.server.dispatcher;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.app.empire.protocol.Protocol;
import com.app.net.ProtocolFactory;
import com.app.protocol.data.DataBeanFilter;
import com.app.protocol.s2s.S2SDecoder;
import com.app.protocol.s2s.S2SEncoder;
import com.app.server.service.ServiceManager;
import com.app.server.session.DispatchSession;
import com.app.session.Session;
import com.app.session.SessionHandler;
import com.app.session.SessionRegistry;

public class IpdServer {
	private PropertiesConfiguration configuration;
	private static final Logger log = Logger.getLogger(IpdServer.class);

	public static void main(String[] args) {
		new IpdServer().launch();
	}

	private void launch() {
		ProtocolFactory.init(Protocol.class, "com.app.empire.protocol.data", "com.app.server.handler");
		try {
			ServiceManager serviceManager = ServiceManager.getManager();
			serviceManager.initService();
			this.configuration = serviceManager.getConfiguration();

			openServerListener();
			openSetverListServlet();
			// ServiceManager.getManager().getAccountSkeleton().connect();
			System.out.println("服务分区公告器启动!");
		} catch (Exception e) {
			log.error("server dispatcher launch failed!", e);
			e.printStackTrace();
		}
	}

	private void openSetverListServlet() throws Exception {
		Server server = new Server();
		// // 设置jetty线程池
		// BoundedThreadPool threadPool = new BoundedThreadPool();
		// // 设置连接参数
		// threadPool.setMinThreads(50);
		// threadPool.setMaxThreads(1000);
		// 设置监听端口，ip地址
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(this.configuration.getInt("http"));
		connector.setHost(this.configuration.getString("localip"));
		server.addConnector(connector);
		// 访问项目地址
		Context root = new Context(server, "/", 1);
		root.addServlet(new ServletHolder(new DispatcherServlet()), "/");// http://192.168.1.11:6887/?area=CN&group=G1&serverid=1
		root.addServlet(new ServletHolder(new ServerLoadServlet()), "/load/*");// http://192.168.1.11:6887/load?area=CN&group=G3
		/**
		 * Get current online user of a specific server parameter:serverid add by zengxc 2014-3-14
		 */
		root.addServlet(new ServletHolder(new GetCCUServlet()), "/GetCCU/*");
		server.start();
		log.info("服务分区公告器servlet启动");
	}

	/**
	 * 打开服务器监听
	 * 
	 * @throws IOException
	 */
	private void openServerListener() throws IOException {
		// session注册
		SessionRegistry registry = new SessionRegistry();
		SessionHandler sessionHandler = new DispatchSessionHandler(registry);
		NioSocketAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		SocketSessionConfig cfg = acceptor.getSessionConfig();
		cfg.setIdleTime(IdleStatus.BOTH_IDLE, 180);
		cfg.setTcpNoDelay(true);
		cfg.setReuseAddress(true);
		DefaultIoFilterChainBuilder filterChainBuilder = acceptor.getFilterChain();
		filterChainBuilder.addFirst("uwap2databean", new DataBeanFilter());
		filterChainBuilder.addFirst("uwap2codec", new ProtocolCodecFilter(new S2SEncoder(), new S2SDecoder()));
		filterChainBuilder.addLast("threadPool", new ExecutorFilter(1, 4));
		acceptor.setHandler(sessionHandler);
		acceptor.setDefaultLocalAddress(new InetSocketAddress(this.configuration.getString("localip"), this.configuration.getInt("port")));
		acceptor.bind();
		log.info("服务分区公告器启动!");
	}

	/**
	 * 内部类 <code>DispatchSessionHandler</code>ip 分配器相关Handler
	 * 
	 * @see com.app.session.SessionHandler
	 * @since JDK 1.6
	 */
	class DispatchSessionHandler extends SessionHandler {
		public DispatchSessionHandler(SessionRegistry paramSessionRegistry) {
			super(paramSessionRegistry);
		}

		public Session createSession(IoSession session) {
			DispatchSession ret = new DispatchSession(session);
			return ret;
		}

		// @Override
		// public void inputClosed(IoSession arg0) throws Exception {
		// // TODO Auto-generated method stub
		//
		// }
	}
}
