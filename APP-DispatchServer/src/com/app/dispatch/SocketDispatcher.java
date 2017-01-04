package com.app.dispatch;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.util.StringUtils;

import com.app.dispatch.data.ClientDecoder;
import com.app.dispatch.data.ClientEncoder;
import com.app.dispatch.data.Packet;
import com.app.dispatch.data.ServerWYDDecoder;
import com.app.dispatch.data.ServerWYDEncoder;
import com.app.dispatch.handler.ClientSessionHandler;
import com.app.dispatch.handler.ScenceServerSessionHandler;
import com.app.dispatch.handler.WorldServerSessionHandler;
import com.app.dispatch.vo.ClientInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.data.account.Heartbeat;
import com.app.empire.protocol.pb.army.ArmyInfoMsgProto.ArmyInfoMsg;
import com.app.protocol.INetData;
import com.app.protocol.s2s.S2SData;
import com.app.protocol.s2s.S2SSegment;
import com.google.protobuf.InvalidProtocolBufferException;

public class SocketDispatcher implements Dispatcher, Runnable {
	public final Logger log = Logger.getLogger(SocketDispatcher.class);
	private AtomicInteger ids = new AtomicInteger(0);
	private AllPlayer allPlayer = AllPlayer.getAllPlayer();
	private ChannelService channelService = null;
	/** worldServer Iosession */
	private IoSession worldServerSession = null;
	private IoSession sceneServerSession = null;
	/** 允许加载的ip段 */
	private TrustIpService trustIpService = null;
	private Configuration configuration = null;
	private Object lock = new Object();

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	// private SocketAddress address;
	private boolean shutdown = false;
	public static Heartbeat heartbeat = new Heartbeat(); // 心跳

	public SocketDispatcher(Configuration configuration) {
		this.configuration = configuration;
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setName("OnlinePrinter");
		thread.start();
	}

	/** 设置通道服务 */
	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	/** 转发数据至 worldServer */
	public void dispatchToWorldServer(int sessionId, IoBuffer buffer) {
		buffer.putInt(0, sessionId);// sessionId
		if (this.worldServerSession != null) {
			this.worldServerSession.write(buffer.duplicate());// 发送worldServer
		} else {
			synchronized (lock) {
				if (this.worldServerSession == null) {
					this.connectWorldServer();
				}
			}
			log.error("world服务器已经断开连接。");
			System.out.println("world服务器已经断开连接。");
		}
	}

	/** 转发数据至 scenceServer */
	public void dispatchToScenceServer(int sessionId, IoBuffer buffer) {
		buffer.order(ByteOrder.LITTLE_ENDIAN);// 设置小头在前 默认大头序
		buffer.putInt(0, sessionId);// sessionId
		if (this.sceneServerSession != null) {
			this.sceneServerSession.write(buffer.duplicate());
		} else {
			synchronized (lock) {
				if (this.sceneServerSession == null) {
					this.connectSceneServer();
				}
			}
			log.error("场景服务器已经断开连接。");
			System.out.println("场景服务器已经断开连接。");
		}
	}

	public void dispatchToScenceServer(IoBuffer buffer) {
		if (this.sceneServerSession != null) {
			this.sceneServerSession.write(buffer.duplicate());
		} else {
			synchronized (lock) {
				if (this.sceneServerSession == null) {
					this.connectSceneServer();
				}
			}
			log.error("场景服务器已经断开连接。");
			System.out.println("场景服务器已经断开连接。");
		}
	}

	/**
	 * 检查协议上行数量是否正常 心跳， 正常协议发送频率
	 * 
	 * @param session
	 * @param type
	 * @param subType
	 * @return true正常false异常
	 */
	public boolean checkProtocol(IoSession session, Short type, Short subType) {
		ClientInfo client = (ClientInfo) session.getAttribute(Constant.CLIENTINFO_KEY);
		long nowTime = System.currentTimeMillis();
		if (client == null) {
			session.close(true);
			return false;
		}

		if (nowTime - client.getProtocolTime() <= 1000 && client.getProtocolType() == type && client.getProtocolSubType() == subType) {// 除战斗
			client.addProtocolCount();
			if (client.getProtocolCount() > this.configuration.getInt("protocolCount")) {// 1秒钟内协议大于15则断开连接
				log.info("Warning 协议超次数，SessionId [" + session.getId() + "] ProtocolCount: + " + client.getProtocolCount());
				session.close(true);
				return false;
			}
		} else {
			client.setProtocolCount(0);
			client.setProtocolTime(nowTime);
		}
		client.setProtocolType(type);
		client.setProtocolSubType(subType);
		return true;
	}

	public IoSession getSession(int sessionId) {
		return allPlayer.getSessions().get(sessionId);
	}

	/** 发数据到 worldServer */
	public void sendControlSegment(S2SSegment seg) {
		try {
			seg.setSessionId(-1);
			this.worldServerSession.write(IoBuffer.wrap(seg.getPacketByteArray()));
		} catch (NullPointerException e) {
			log.info("this.serverSession is null.");
			if (this.worldServerSession == null) {
				this.connectWorldServer();
			}
		}
	}

	/**
	 * 实现DispatchServer连接WorldServer，这时DispatchServer做为Client端
	 * 
	 * @param address 连接地址
	 * @param config 配置信息
	 * @return ConnectFuture 连接状态
	 */
	public ConnectFuture connectWorldServer() {
		int worldreceivebuffsize = this.configuration.getInt("worldreceivebuffsize");
		int worldwritebuffsize = this.configuration.getInt("worldwritebuffsize");
		String worldIp = this.configuration.getString("worldip");
		int worldPort = this.configuration.getInt("worldport");
		InetSocketAddress address = new InetSocketAddress(worldIp, worldPort);

		NioSocketConnector connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
		SocketSessionConfig cfg = connector.getSessionConfig();
		cfg.setIdleTime(IdleStatus.BOTH_IDLE, 120);
		cfg.setTcpNoDelay(true);
		cfg.setReceiveBufferSize(worldreceivebuffsize);
		cfg.setSendBufferSize(worldwritebuffsize);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerWYDEncoder(), new ServerWYDDecoder()));
		connector.getFilterChain().addLast("threadPool", new ExecutorFilter(1, 4));
		connector.setHandler(new WorldServerSessionHandler(this));
		connector.setDefaultRemoteAddress(address);
		ConnectFuture future = connector.connect();
		future.awaitUninterruptibly();

		if (this.worldServerSession == null || !this.worldServerSession.isConnected())
			System.out.println("WorldServer 连接失败!");
		else
			System.out.println("WorldServer 连接成功!");

		return future;
	}

	/**
	 * 链接scene服
	 * 
	 * @return
	 */
	public ConnectFuture connectSceneServer() {
		int scenereceivebuffsize = this.configuration.getInt("scenereceivebuffsize");
		int scenewritebuffsize = this.configuration.getInt("scenewritebuffsize");
		String sceneIp = this.configuration.getString("sceneip");
		int scenePort = this.configuration.getInt("sceneport");
		InetSocketAddress address = new InetSocketAddress(sceneIp, scenePort);

		NioSocketConnector connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
		SocketSessionConfig cfg = connector.getSessionConfig();
		cfg.setIdleTime(IdleStatus.BOTH_IDLE, 120);
		cfg.setTcpNoDelay(true);
		cfg.setReceiveBufferSize(scenereceivebuffsize);
		cfg.setSendBufferSize(scenewritebuffsize);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerWYDEncoder(), new ServerWYDDecoder()));
		connector.getFilterChain().addLast("threadPool", new ExecutorFilter(1, 4));
		connector.setHandler(new ScenceServerSessionHandler(this));
		connector.setDefaultRemoteAddress(address);
		ConnectFuture future = connector.connect();
		future.awaitUninterruptibly();

		if (this.sceneServerSession == null || !this.sceneServerSession.isConnected())
			System.out.println("Scene Server 连接失败!");
		else
			System.out.println("Scene Server 连接成功!");

		return future;
	}

	/**
	 * 实现DispatchServer 监听客户端请求
	 * 
	 * @param address 套接字地址
	 * @param clientreceivebuffsize 输入缓冲区大小
	 * @param clientwritebuffsize 输出缓冲区大小
	 * @throws IOException 绑定监听出错时抛出些异常
	 */
	public void bind() throws IOException {
		int clientreceivebuffsize = configuration.getInt("clientreceivebuffsize");
		int clientwritebuffsize = configuration.getInt("clientwritebuffsize");
		String ip = configuration.getString("localip");
		int port = configuration.getInt("port");

		SocketAddress address = new InetSocketAddress(ip, port);
		NioSocketAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		SocketSessionConfig cfg = acceptor.getSessionConfig();
		cfg.setIdleTime(IdleStatus.BOTH_IDLE, 80);
		// cfg.setReuseAddress(true);
		cfg.setTcpNoDelay(true);
		cfg.setReceiveBufferSize(clientreceivebuffsize);
		cfg.setSendBufferSize(clientwritebuffsize);
		// 添加Protocol编码过滤器
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientEncoder(), new ClientDecoder()));
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(1, 4));
		acceptor.setHandler(new ClientSessionHandler(this));
		acceptor.setDefaultLocalAddress(address);
		acceptor.bind();
	}

	/** 转发数据至前端 */
	public void dispatchToClient(Packet packet) {
		int sessionId = packet.sessionId;
		IoBuffer buffer = packet.buffer;
		IoSession session = (IoSession) allPlayer.getSessions().get(Integer.valueOf(sessionId));
		if (session != null) {
			buffer.order(ByteOrder.LITTLE_ENDIAN);// 设置小头在前 默认大头序
			short type = packet.getpType();
			if (type == Protocol.MAIN_ACCOUNT) {
				short subType = packet.getpSubType();
				if (subType == Protocol.ACCOUNT_LoginOk) {// 账号登录成功
					session.setAttribute(Constant.LOGINMARK_KEY, Constant.LOGINMARK_LOGED);
					SocketDispatcher.this.channelService.getWorldChannel().join(session);// 加入到世界频道
				} else if (subType == Protocol.ACCOUNT_RoleLoginOk) {// 角色登录成功
					try {
						INetData udata = new S2SData(type, subType, Arrays.copyOfRange(buffer.array(), 18, buffer.array().length), 0, sessionId, packet.getTarget(),
								packet.getProType());
						ArmyInfoMsg msg = ArmyInfoMsg.parseFrom(udata.readBytes());
						int playerId = msg.getPlayerInfo().getPlayerId();
						session.setAttribute(Constant.SESSIONID, playerId);
						allPlayer.getSessions().put(playerId, session);
						allPlayer.getSessions().remove(sessionId);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvalidProtocolBufferException e) {
						e.printStackTrace();
					}
				}
			}
			session.write(buffer);
		}
	}

	/** 用户上线，注册客户端 */
	public void registerClient(IoSession session) {
		Integer sessionId = this.ids.decrementAndGet();
		if (sessionId < 0) {
			log.info("用户链接SessionId: " + sessionId);
		}
		// session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);// 空闲时间60秒
		session.setAttribute(Constant.SESSIONID, sessionId);
		allPlayer.getSessions().put(sessionId, session);

		session.setAttribute(Constant.LOGINMARK_KEY, Constant.LOGINMARK_UNLOG);
		// 设置客户端消息初始化
		session.setAttribute(Constant.CLIENTINFO_KEY, new ClientInfo(session));

		this.channelService.getWorldChannel().join(session);
		// ====发送ip地址到worldserver===
		InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
		if (address == null) {
			log.error("远程地址获取失败.......账号注册失败。");
			session.close(true);
			return;
		}
		String hostAddress = address.getAddress().getHostAddress();
		if (StringUtils.hasText(hostAddress)) {
			S2SSegment seg = new S2SSegment((byte) Protocol.MAIN_SERVER, (byte) Protocol.SERVER_SetClientIPAddress);
			seg.writeInt(sessionId.intValue());
			seg.writeString(hostAddress);
			sendControlSegment(seg);
		}
		// =======
	}

	/** 用户下线，注销客户端 */
	public void unRegisterClient(IoSession session) {
		if ((Boolean) session.getAttribute(Constant.LOGINMARK_KEY)) {
			// this.channelService.getWorldChannel().removeSession(session);
			this.channelService.removeSessionFromAllChannel(session);// 所有频道中移除此session
		}
		Integer sessionId = (Integer) session.getAttribute(Constant.SESSIONID);
		if (sessionId != null) {
			S2SSegment seg = new S2SSegment((byte) Protocol.MAIN_SERVER, (byte) Protocol.SERVER_SessionClosed);
			seg.writeInt(sessionId.intValue());
			sendControlSegment(seg);
			allPlayer.getSessions().remove(sessionId);
		}
	}

	/** 提玩家下线 */
	public void unRegisterClient(int sessionId) {
		IoSession session = (IoSession) allPlayer.getSessions().remove(Integer.valueOf(sessionId));
		if ((session != null) && (session.isConnected()))
			session.close(true);
	}

	/** 执行worldServer 发来的协议 */
	public void processControl(Packet packet) {
		TimeControlProcessor.getControlProcessor().process(packet.data);
	}

	/** 广播线上所有用户 */
	public void broadcast(IoBuffer buffer) {
		for (IoSession session : allPlayer.getSessions().values()) {
			session.write(buffer.duplicate());
		}
		buffer.clear();
	}

	/** 设置允许加载的ip段 */
	public void setTrustIpService(TrustIpService trustIpService) {
		this.trustIpService = trustIpService;
		log.info("add trustIpService: " + this.trustIpService);
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(60000L);
			} catch (InterruptedException ex) {
			}
			log.info("ONLINE Session[" + allPlayer.getSessions().size() + "]");
		}
	}

	public IoSession getWorldServerSession() {
		return worldServerSession;
	}

	public void setWorldServerSession(IoSession worldServerSession) {
		this.worldServerSession = worldServerSession;
	}

	public IoSession getSceneServerSession() {
		return sceneServerSession;
	}

	public void setSceneServerSession(IoSession sceneServerSession) {
		this.sceneServerSession = sceneServerSession;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	public TrustIpService getTrustIpService() {
		return trustIpService;
	}

}