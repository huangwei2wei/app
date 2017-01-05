package com.app.net;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.app.protocol.data.AbstractData;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.handler.IDataHandler;
import com.google.protobuf.Message;

public abstract class Connector implements IConnector {
	protected static final Logger log = Logger.getLogger(Connector.class);
	protected InetSocketAddress address;
	protected String userName = "";
	protected String password = "";
	protected boolean needRetry = false;
	protected NioSocketConnector connector;
	protected SocketSessionConfig config;
	protected int receiveBufferSize = 65534;
	protected int sendBufferSize = 65534;
	protected IoSession session;
	protected String id;

	public Connector(String id, InetSocketAddress address) {
		this.id = id;
		this.address = address;
		initConnector();
	}

	public String getId() {
		return this.id;
	}

	/**
	 * 初始化连接器各基本参数
	 */
	private void initConnector() {
		this.connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
		this.config = connector.getSessionConfig();
		this.config.setTcpNoDelay(true);
		this.config.setSendBufferSize(this.sendBufferSize);
		this.config.setReceiveBufferSize(this.receiveBufferSize);
		init();
	}

	public abstract void init();

	public void connect() throws ConnectException {
		if (this.isConnected()) {
			throw new IllegalStateException("connection is connected");
		}
		// 设置连接超时检查时间
		// this.connector.setConnectTimeoutCheckInterval(30);
		// 设置事件处理器
		this.connector.setHandler(new OriginalSessionHandler());
		// 建立连接
		ConnectFuture future = connector.connect(this.address);
		// 等待连接创建完成
		future.awaitUninterruptibly();
	}

	public boolean isConnected() {
		if (this.session == null)
			return false;
		return this.session.isConnected();
	}

	public void send(AbstractData data) {
		if (this.isConnected() == false) {
			this.connector.connect(this.address);
			log.info("重连.");
		}
		this.session.write(data);
	}

	public void send(short type, short subType, Message msg, byte target) {
		PbAbstractData pbMsg = new PbAbstractData(type, subType, target);
		pbMsg.setBytes(msg.toByteArray());
		send(pbMsg);
	}

	public void send(short type, short subType, int sessionId, int serial, Message msg, byte target) {
		PbAbstractData pbMsg = new PbAbstractData(type, subType, sessionId, serial, target);
		pbMsg.setBytes(msg.toByteArray());
		send(pbMsg);
	}

	public void close() {
		if ((this.session != null) && (this.session.isConnected())) {
			this.session.close(true);
		}
	}

	public SocketAddress getRemoteAddress() {
		return this.session.getRemoteAddress();
	}

	protected abstract void connected();

	protected abstract void idle();

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNeedRetry(boolean needRetry) {
		this.needRetry = needRetry;
	}

	public boolean isNeedRetry() {
		return this.needRetry;
	}

	/**
	 * 原始的会话处理
	 * 
	 */
	public class OriginalSessionHandler extends IoHandlerAdapter {
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			Connector.log.info(cause, cause);
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			AbstractData data = (AbstractData) message;
			if (data == null) {
				Connector.log.error("get a NULL data!");
			} else {
				IDataHandler handler = ProtocolFactory.getDataHandler(data);
				if (handler != null) {
					data.setSource(Connector.this);
					handler.handle(data);
				} else {
					Connector.log.error("get a NULL handler!");
				}
			}
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			// super.sessionClosed(session);
			// 断线重连
			if (Connector.this.needRetry) {
				while (true) {
					if (Connector.this.isConnected())
						return;
					try {
						Thread.sleep(20000L);
						// Connector.this.initConnector();
						// Connector.this.connect();
						Connector.this.connector.connect();
						System.out.println("断线尝试重连...");
						Connector.log.info("log断线尝试重连...");
					} catch (Exception e) {
						Connector.log.error(e.getMessage());
					}
				}
			}
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			// super.sessionCreated(session);
			Connector.this.session = session;
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			// super.sessionOpened(session);
			// Connector.this.session = session;
			Connector.this.connected();
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			super.sessionIdle(session, status);
			Connector.this.idle();
		}
	}
}
