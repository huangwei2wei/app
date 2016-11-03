package com.app.dispatch;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
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
import com.app.dispatch.vo.ClientInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.data.account.Heartbeat;
import com.app.protocol.ProtocolManager;
import com.app.protocol.s2s.S2SSegment;

public class SocketDispatcher implements Dispatcher, Runnable {
	private static final String ATTRIBUTE_STRING = "SESSIONID";
	private static final Logger log = Logger.getLogger(SocketDispatcher.class);
	private AtomicInteger ids = new AtomicInteger(0);
	private ConcurrentHashMap<Integer, IoSession> sessions = new ConcurrentHashMap<Integer, IoSession>();// sessionID-->客户端IoSession
	// private ConcurrentHashMap<Integer, ClientInfo> allClientInfo = new ConcurrentHashMap<Integer, ClientInfo>();// playerId-->客户端

	private ChannelService channelService = null;
	private NioSocketAcceptor acceptor = null;
	private NioSocketConnector connector = null;
	/** worldServer Iosession */
	private IoSession worldServerSession = null;
	private IoSession sceneServerSession = null;
	/** 允许加载的ip段 */
	private TrustIpService trustIpService = null;
	private Configuration configuration = null;
	public static final String SERVERID = "serverid";
	public static final String SERVERNAME = "servername";
	public static final String SERVERPASSWORD = "serverpassword";
	private SocketAddress address;
	private boolean shutdown = false;
	private Heartbeat heartbeat = new Heartbeat();// 心跳
	private static final String LOGINMARK_KEY = "ISLOGED";// 账号登录成功设为true
	protected static final String CLIENTINFO_KEY = "CLIENTINFO";
	protected static final String PLAYERID_KEY = "PLAYERID";
	private static final boolean LOGINMARK_UNLOG = false;
	private static final boolean LOGINMARK_LOGED = true;

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

	/** 设置允许加载的ip段 */
	public void setTrustIpService(TrustIpService trustIpService) {
		this.trustIpService = trustIpService;
		log.info("add trustIpService: " + this.trustIpService);
	}

	/** 处理client发来的数据 */
	public void processClientData(IoSession session, Object object) {
		Integer sessionId = (Integer) session.getAttribute(ATTRIBUTE_STRING);
		if (sessionId == null) {
			session.close(true);
			return;
		}
		IoBuffer buffer = (IoBuffer) object;
		buffer.order(ByteOrder.LITTLE_ENDIAN);// 设置小头在前　默认大头序
		byte type = buffer.get(15);
		byte subType = buffer.get(16);

		if (!checkProtocol(session, type, subType)) // 协议检查
			return;

		if (type > 100) {
			dispatchToScenceServer(sessionId, buffer);
		} else {// 发world 服
			if (type == Protocol.MAIN_ACCOUNT) {
				if (subType == Protocol.ACCOUNT_Heartbeat && (Boolean) session.getAttribute(LOGINMARK_KEY)) {// 回应客户端心跳协议
					heartbeat.setServiceTime(System.currentTimeMillis());
					IoBuffer byteBuffer = IoBuffer.wrap(ProtocolManager.makeSegment(heartbeat).getPacketByteArray());
					session.write(byteBuffer.duplicate());
				} else {
					dispatchToWorldServer(sessionId, buffer);
				}
			} else if ((Boolean) session.getAttribute(LOGINMARK_KEY) || type == Protocol.MAIN_SYSTEM) {// 判断用户是否已经登录或者为登录协议
				dispatchToWorldServer(sessionId, buffer);
			} else {// 不是心跳，不是登录协议，并且用户未登录则断开socket连接
				log.info("用户未登录Kill Session LOGINMARK:" + session.getAttribute(LOGINMARK_KEY) + "---type:" + type + "---subtype:" + subType);
				session.close(true);
			}
		}
	}

	/** 转发数据至 worldServer */
	public void dispatchToWorldServer(int sessionId, IoBuffer buffer) {
		buffer.putInt(0, sessionId);// sessionId
		this.worldServerSession.write(buffer.duplicate());// 发送worldServer
	}

	/** 转发数据至 scenceServer */
	public void dispatchToScenceServer(int sessionId, IoBuffer buffer) {
		buffer.putInt(4, sessionId);// sessionId
		this.sceneServerSession.write(buffer.duplicate());
	}

	/**
	 * 检查协议上行数量是否正常 心跳， 正常协议发送频率
	 * 
	 * @param session
	 * @param type
	 * @param subType
	 * @return true正常false异常
	 */
	public boolean checkProtocol(IoSession session, byte type, byte subType) {
		ClientInfo client = (ClientInfo) session.getAttribute(CLIENTINFO_KEY);
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
		return this.sessions.get(sessionId);
	}

	/** 发数据到 worldServer */
	public void sendControlSegment(S2SSegment seg) {
		try {
			seg.setSessionId(-1);
			this.worldServerSession.write(IoBuffer.wrap(seg.getPacketByteArray()));
		} catch (NullPointerException e) {
			log.info("this.serverSession is null.");
			if (this.worldServerSession == null) {
				this.connectWoeldServer();
			}
		}
	}

	/**
	 * 实现DispatchServer连接WorldServer，这时DispatchServer做为Client端
	 * 
	 * @param address
	 *            连接地址
	 * @param config
	 *            配置信息
	 * @return ConnectFuture 连接状态
	 */
	public ConnectFuture connectWoeldServer() {
		int worldreceivebuffsize = this.configuration.getInt("worldreceivebuffsize");
		int worldwritebuffsize = this.configuration.getInt("worldwritebuffsize");
		String worldIp = this.configuration.getString("worldip");
		int worldPort = this.configuration.getInt("worldport");
		InetSocketAddress address = new InetSocketAddress(worldIp, worldPort);

		this.connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
		SocketSessionConfig cfg = this.connector.getSessionConfig();
		cfg.setIdleTime(IdleStatus.BOTH_IDLE, 120);
		cfg.setTcpNoDelay(true);
		cfg.setReceiveBufferSize(worldreceivebuffsize);
		cfg.setSendBufferSize(worldwritebuffsize);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerWYDEncoder(), new ServerWYDDecoder()));
		connector.getFilterChain().addLast("threadPool", new ExecutorFilter(1, 4));
		connector.setHandler(new WorldServerSessionHandler());
		connector.setDefaultRemoteAddress(address);
		ConnectFuture future = this.connector.connect();
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

		this.connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
		SocketSessionConfig cfg = this.connector.getSessionConfig();
		cfg.setIdleTime(IdleStatus.BOTH_IDLE, 120);
		cfg.setTcpNoDelay(true);
		cfg.setReceiveBufferSize(scenereceivebuffsize);
		cfg.setSendBufferSize(scenewritebuffsize);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerWYDEncoder(), new ServerWYDDecoder()));
		connector.getFilterChain().addLast("threadPool", new ExecutorFilter(1, 4));
		connector.setHandler(new ScenceServerSessionHandler());
		connector.setDefaultRemoteAddress(address);
		ConnectFuture future = this.connector.connect();
		future.awaitUninterruptibly();

		if (this.worldServerSession == null || !this.worldServerSession.isConnected())
			System.out.println("Scene Server 连接失败!");
		else
			System.out.println("Scene Server 连接成功!");

		return future;
	}

	/**
	 * 实现DispatchServer 监听客户端请求
	 * 
	 * @param address
	 *            套接字地址
	 * @param clientreceivebuffsize
	 *            输入缓冲区大小
	 * @param clientwritebuffsize
	 *            输出缓冲区大小
	 * @throws IOException
	 *             绑定监听出错时抛出些异常
	 */
	public void bind() throws IOException {
		int clientreceivebuffsize = configuration.getInt("clientreceivebuffsize");
		int clientwritebuffsize = configuration.getInt("clientwritebuffsize");
		String ip = configuration.getString("localip");
		int port = configuration.getInt("port");

		this.address = new InetSocketAddress(ip, port);
		this.acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		SocketSessionConfig cfg = acceptor.getSessionConfig();
		cfg.setIdleTime(IdleStatus.BOTH_IDLE, 80);
		// cfg.setReuseAddress(true);
		cfg.setTcpNoDelay(true);
		cfg.setReceiveBufferSize(clientreceivebuffsize);
		cfg.setSendBufferSize(clientwritebuffsize);
		// 添加Protocol编码过滤器
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientEncoder(), new ClientDecoder()));
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(1, 4));
		acceptor.setHandler(new ClientSessionHandler());
		acceptor.setDefaultLocalAddress(address);
		this.acceptor.bind();
	}

	/** 转发数据至前端 */
	@SuppressWarnings("unused")
	public void dispatchToClient(Packet packet) {
		int sessionId = packet.sessionId;
		IoBuffer buffer = packet.buffer;
		IoSession session = (IoSession) this.sessions.get(Integer.valueOf(sessionId));
		if (session != null) {
			byte type = buffer.get(14);
			if (type == Protocol.MAIN_ACCOUNT) {
				byte subType = buffer.get(15);
				if (subType == Protocol.ACCOUNT_LoginOk) {// 账号登录成功
					session.setAttribute(LOGINMARK_KEY, LOGINMARK_LOGED);
					SocketDispatcher.this.channelService.getWorldChannel().join(session);// 加入到世界频道
				}
				// else if (subType == Protocol.ACCOUNT_RoleLoginOk) {// 角色登录成功添加Player对象
				// try {
				// ClientInfo client = (ClientInfo) session.getAttribute(CLIENTINFO_KEY);
				// if (client == null)
				// throw new Exception("ClientInfo is null");
				// // INetData udata = new S2SData(Arrays.copyOfRange(buffer.array(), 18, buffer.array().length), 1, sessionId);
				// // int playerId = udata.readInt();// 角色id
				// // int heroId = udata.readInt();// 英雄id
				// // String nickname = udata.readString();// 玩家角色名称
				// // int lv = udata.readInt(); // 玩家等级
				// // int lvExp = udata.readInt(); // 玩家vip等级
				// // int vipLv = udata.readInt(); // 玩家vip等级
				// // int vipExp = udata.readInt(); // 玩家vip经验
				// // int fight = udata.readInt(); // 玩家当前战斗力
				// // int diamond = udata.readInt(); // 钻石
				// // int gold = udata.readInt(); // 玩家金币数量
				// // int power = udata.readInt(); // 体力
				// // String property = udata.readString(); // 属性
				// //
				// // Player player = new Player(playerId);
				// // // player.setHeroId(heroId);
				// // player.setNickname(nickname);
				// // player.setLv(lv);
				// // player.setVipLv(vipLv);
				// // player.setProperty(property);
				// // player.setFight(fight);
				// // client.setPlayer(player);
				// // session.setAttribute(PLAYERID_KEY, playerId);
				// // allClientInfo.put(playerId, client);
				//
				// } catch (Exception ex) {
				// ex.printStackTrace();
				// log.error(ex, ex);
				// }
				// }
			}
			session.write(buffer);
		}
	}

	/** 用户上线，注册客户端 */
	public void registerClient(IoSession session) {
		Integer sessionId = this.ids.incrementAndGet();
		if (sessionId < 0) {
			log.info("用户链接SessionId: " + sessionId);
		}
		// session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);// 空闲时间60秒
		session.setAttribute(ATTRIBUTE_STRING, sessionId);
		session.setAttribute(LOGINMARK_KEY, LOGINMARK_UNLOG);
		// 设置客户端消息初始化
		session.setAttribute(CLIENTINFO_KEY, new ClientInfo(session));

		this.sessions.put(sessionId, session);
		// this.channelService.getWorldChannel().join(session);
		// ====发送ip地址到worldserver===
		InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
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
	protected void unRegisterClient(IoSession session) {
		if ((Boolean) session.getAttribute(LOGINMARK_KEY)) {
			// this.channelService.getWorldChannel().removeSession(session);
			this.channelService.removeSessionFromAllChannel(session);// 所有频道中移除此session
		}
		Integer sessionId = (Integer) session.getAttribute(ATTRIBUTE_STRING);
		// Integer playerId = (Integer) session.getAttribute(PLAYERID_KEY);
		if (sessionId != null) {
			S2SSegment seg = new S2SSegment((byte) Protocol.MAIN_SERVER, (byte) Protocol.SERVER_SessionClosed);
			seg.writeInt(sessionId.intValue());
			sendControlSegment(seg);
			this.sessions.remove(sessionId);
			// if (playerId != null)
			// this.allClientInfo.remove(playerId);
		}
	}

	/** 提玩家下线 */
	public void unRegisterClient(int sessionId) {
		IoSession session = (IoSession) this.sessions.remove(Integer.valueOf(sessionId));
		if ((session != null) && (session.isConnected()))
			session.close(true);
	}

	/** 执行worldServer 发来的协议 */
	protected void processControl(Packet packet) {
		TimeControlProcessor.getControlProcessor().process(packet.data);
	}

	/** 广播线上所有用户 */
	public void broadcast(IoBuffer buffer) {
		for (IoSession session : this.sessions.values()) {
			session.write(buffer.duplicate());
		}
		buffer.clear();
	}

	/** 重新开启服务 */
	public void shutdown() {
		this.acceptor.unbind();
		try {
			this.acceptor.setHandler(new ClientSessionHandler());
			this.acceptor.setDefaultLocalAddress(this.address);
			this.acceptor.bind();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// /***
	// * 同步玩家角色信息
	// */
	// @Override
	// public void syncPlayer(INetData data) {
	// try {
	// int playerId = data.readInt();// 角色id
	// int heroId = data.readInt();// 英雄id
	// int hp = data.readInt();// 英雄血量
	// String nickname = data.readString();// 玩家角色名称
	// int lv = data.readInt(); // 玩家等级
	// int vipLv = data.readInt(); // 玩家vip等级
	// String property = data.readString();// 角色属性
	// int fight = data.readInt(); // 玩家当前战斗力
	// int roomId = data.readInt();// pvp 房间号
	//
	// ClientInfo clientInfo = this.allClientInfo.get(playerId);
	// Player player = clientInfo.getPlayer();
	// player.setNickname(nickname);
	// player.setLv(lv);
	// player.setVipLv(vipLv);
	// player.setProperty(property);
	// player.setFight(fight);
	// player.setRoomId(roomId);
	// if (roomId > 0) {
	// Hero hero = new Hero();
	// hero.setHp(hp);
	// player.addHero(heroId, hero);
	// } else {
	// player.clearHerod();
	// }
	// } catch (Exception ex) {
	// log.error(ex, ex);
	// }
	//
	// }

	public void run() {
		while (true) {
			try {
				Thread.sleep(60000L);
			} catch (InterruptedException ex) {
			}
			log.info("ONLINE Session[" + this.sessions.size() + "]");
		}
	}

	/**
	 * DispatchServer处理WorldServer数据信息,做为客户端的处理Handler，第一次登陆时注册dispatcher server信息
	 */
	class WorldServerSessionHandler extends IoHandlerAdapter {
		@Override
		public void exceptionCaught(IoSession sesion, Throwable throwable) throws Exception {
			// sesion.close(true);
			SocketDispatcher.log.error(throwable, throwable);
		}

		@Override
		public void messageReceived(IoSession session, Object object) throws Exception {
			Packet packet = (Packet) object;
			if (packet.type == Packet.TYPE.BUFFER) {
				// System.out.println("dis收到WORLD数据发前端或场景服：" + packet.data.toString());
				if (packet.pType > 100)
					dispatchToScenceServer(packet.sessionId, packet.buffer);
				else
					SocketDispatcher.this.dispatchToClient(packet);
			} else {
				System.out.println(System.currentTimeMillis() + " dis收到WORLD数据发系统：" + packet.data.toString());
				SocketDispatcher.this.processControl(packet);
			}
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			worldServerSession = null;
			// 断线重连worldServer
			try {
				Thread.sleep(12000L);
				SocketDispatcher.this.connectWoeldServer();
				SocketDispatcher.log.info("worldServer 断线重连。。。");
			} catch (Exception e) {
				SocketDispatcher.log.error(e.getMessage());
			}
		}

		// I/O processor线程触发
		@Override
		public void sessionCreated(IoSession session) throws Exception {
			worldServerSession = session;
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
			S2SSegment seg = new S2SSegment(Protocol.MAIN_SERVER, Protocol.SERVER_Heartbeat);
			SocketDispatcher.this.sendControlSegment(seg);
		}

		@Override
		public void sessionOpened(IoSession session) {
			S2SSegment seg = new S2SSegment(Protocol.MAIN_SERVER, Protocol.SERVER_DispatchLogin);
			seg.writeString((String) SocketDispatcher.this.configuration.getProperty("area"));
			seg.writeString((String) SocketDispatcher.this.configuration.getProperty("serverpassword"));
			seg.writeInt(SocketDispatcher.this.configuration.getInt("maxplayer"));
			SocketDispatcher.this.sendControlSegment(seg);
		}
	}

	/**
	 * DispatchServer处理Scence Server数据信息,做为客户端的处理Handler，第一次登陆时注册dispatcher server信息
	 */
	class ScenceServerSessionHandler extends IoHandlerAdapter {
		@Override
		public void exceptionCaught(IoSession sesion, Throwable throwable) throws Exception {
			// sesion.close(true);
			SocketDispatcher.log.error(throwable, throwable);
		}

		@Override
		public void messageReceived(IoSession session, Object object) throws Exception {
			Packet packet = (Packet) object;
			if (packet.type == Packet.TYPE.BUFFER) {
				// System.out.println("dis收到WORLD数据发前端：" + packet.data.toString());
				if (packet.pType < 100)
					SocketDispatcher.this.dispatchToWorldServer(packet.sessionId, packet.buffer);
				else
					SocketDispatcher.this.dispatchToClient(packet);
			} else {
				System.out.println(System.currentTimeMillis() + " dis收到WORLD数据发系统：" + packet.data.toString());
				SocketDispatcher.this.processControl(packet);
			}
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			sceneServerSession = null;
			// 断线重连worldServer
			try {
				Thread.sleep(12000L);
				SocketDispatcher.this.connectWoeldServer();
				SocketDispatcher.log.info("Scene Server 断线重连。。。");
			} catch (Exception e) {
				SocketDispatcher.log.error(e.getMessage());
			}
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			sceneServerSession = session;
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
			S2SSegment seg = new S2SSegment(Protocol.MAIN_SERVER, Protocol.SERVER_Heartbeat);
			SocketDispatcher.this.sendControlSegment(seg);
		}

		@Override
		public void sessionOpened(IoSession session) {
		}
	}

	/**
	 * DispatchServer做为服务器端的处理Handler，处理手机客户端的数据信息
	 */
	class ClientSessionHandler extends IoHandlerAdapter {
		@Override
		public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
			SocketDispatcher.log.error(throwable, throwable);
			session.close(true);
		}

		@Override
		public void messageReceived(IoSession session, Object object) throws Exception {
			SocketDispatcher.this.processClientData(session, object);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			if (!(SocketDispatcher.this.shutdown))
				SocketDispatcher.this.unRegisterClient(session);
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			// 判断允许加载的ip
			InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
			if (!(SocketDispatcher.this.trustIpService.isTrustIp(address))) {
				System.out.println("ip 不在允许的范围" + session);
				SocketDispatcher.log.info("ip 不在允许的范围" + session);
				session.close(true);
			}
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
			SocketDispatcher.this.registerClient(session);
			log.info("ok:" + address.getAddress().getHostAddress());
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
			session.close(true);
		}
	}

}