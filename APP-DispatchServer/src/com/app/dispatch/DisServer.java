package com.app.dispatch;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;

import com.app.empire.protocol.Protocol;
import com.app.net.ProtocolFactory;

public class DisServer {
	private static final Logger log = Logger.getLogger(DisServer.class);
	/** 配置信息 */
	public static ConfigMenger configuration = null;
	/** 分发服务 */
	private Dispatcher dispatcher = null;
	/** 通道服务 */
	private ChannelService channelService = null;
	/** 控制处理器 */
	private TimeControlProcessor controlProcessor = null;
	/** IP控制器 */
	private TrustIpService trustIpService = null;
	/** 分服服务 */
	private IpdService ipdService = null;
	// /** 同屏同步服务 */
	// private SyncService syncService = new SyncService();

	public static void main(String[] args) throws Throwable {
		DisServer main = new DisServer();
		main.launch();
	}

	/**
	 * 启动服务
	 * 
	 * @throws Exception
	 */
	private void launch() throws Exception {
		// 初始化协议接口
		ProtocolFactory.init(Protocol.class, "com.app.empire.protocol.data", "com.app.empire.server.handler");
		// 加载配置文件
		configuration = new ConfigMenger("configDispatch.properties");
		// 创建通道服务
		this.channelService = new ChannelService();
		// 同屏同步服务

		// 控制处理器初始化,启动Control线程，监听
		this.controlProcessor = TimeControlProcessor.getControlProcessor();
		this.controlProcessor.start();
		this.controlProcessor.setChannelService(this.channelService);
		this.controlProcessor.setConfiguration(configuration);
		// 从配置文件中读取服务类型
		String serverType = configuration.getConfiguration().getString("servertype");
		// 加载允许访问ip段
		this.trustIpService = new TrustIpService(serverType);
		// 连接ipd server
		this.ipdService = new IpdService(configuration.getConfiguration());
		this.controlProcessor.setIpdService(this.ipdService);

		int worldreceivebuffsize = configuration.getConfiguration().getInt("worldreceivebuffsize");
		int worldwritebuffsize = configuration.getConfiguration().getInt("worldwritebuffsize");
		if ("socket".equals(serverType)) {
			// dis 监听端口链接客户端
			this.dispatcher = new SocketDispatcher(configuration.getConfiguration());
			((SocketDispatcher) this.dispatcher).start();
			((SocketDispatcher) this.dispatcher).setChannelService(this.channelService);
			((SocketDispatcher) this.dispatcher).setTrustIpService(this.trustIpService);
			// ((SocketDispatcher) this.dispatcher).setSyncService(this.syncService);

			this.controlProcessor.setDispatcher(this.dispatcher);
			// 配置
			int clientreceivebuffsize = configuration.getConfiguration().getInt("clientreceivebuffsize");
			int clientwritebuffsize = configuration.getConfiguration().getInt("clientwritebuffsize");
			String ip = configuration.getConfiguration().getString("localip");
			int port = configuration.getConfiguration().getInt("port");
			((SocketDispatcher) this.dispatcher).bind(new InetSocketAddress(ip, port), clientreceivebuffsize, clientwritebuffsize);
			log.info("binded");
			// 链接world
			ConnectFuture future = ((SocketDispatcher) this.dispatcher).connect();
			// 阻塞数据，直到确定与world server连接成功
			future.awaitUninterruptibly();
			log.info("数据分发服务器启动完成  -- 端口:" + configuration.getConfiguration().getInt("port"));
		} else {
			ConnectFuture future = null;
			if ("http".equals(serverType)) {
				this.dispatcher = new HttpDispatcher(this.controlProcessor, configuration.getConfiguration());
				((HttpDispatcher) this.dispatcher).start();
				((HttpDispatcher) this.dispatcher).setChannelService(this.channelService);
				((HttpDispatcher) this.dispatcher).setTrustIpService(this.trustIpService);
				this.controlProcessor.setDispatcher(this.dispatcher);
				((HttpDispatcher) this.dispatcher).bind(configuration.getConfiguration().getString("localip"), configuration.getConfiguration().getInt("port"));
				log.info("binded");
				future = ((HttpDispatcher) this.dispatcher).connect(new InetSocketAddress(configuration.getConfiguration().getString("worldip"), configuration.getConfiguration().getInt("worldport")),
						worldreceivebuffsize, worldwritebuffsize);
				future.awaitUninterruptibly();
				log.info("Http Dispatch Started");
				log.info("数据分发服务器启动完成  -- 端口:" + configuration.getConfiguration().getInt("port"));
			} else if ("singlesocket".equals(serverType)) {
				this.dispatcher = new SingleSocketDispatcher(this.controlProcessor, configuration.getConfiguration());
				((SingleSocketDispatcher) this.dispatcher).setChannelService(this.channelService);
				((SingleSocketDispatcher) this.dispatcher).setTrustIpService(this.trustIpService);
				((SingleSocketDispatcher) this.dispatcher).setIpdService(this.ipdService);
				this.controlProcessor.setDispatcher(this.dispatcher);
				((SingleSocketDispatcher) this.dispatcher).bind(new InetSocketAddress(configuration.getConfiguration().getString("localip"), configuration.getConfiguration().getInt("port")),
						worldreceivebuffsize, worldwritebuffsize);
				log.info("binded");
				future = ((SingleSocketDispatcher) this.dispatcher).connect(
						new InetSocketAddress(configuration.getConfiguration().getString("worldip"), configuration.getConfiguration().getInt("worldport")), worldreceivebuffsize, worldwritebuffsize);
				future.awaitUninterruptibly();
				log.info("SingleSocket Dispatch Started");
			} else {
				throw new RuntimeException("Unknow Server Type");
			}
		}
		System.out.println("数据分发服务器启动完成  -- 端口:" + configuration.getConfiguration().getInt("port"));
	}
}