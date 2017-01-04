package com.app.dispatch.handler;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.app.dispatch.Constant;
import com.app.dispatch.SocketDispatcher;
import com.app.empire.protocol.Protocol;
import com.app.protocol.ProtocolManager;

public class ClientSessionHandler extends IoHandlerAdapter {
	public final Logger log = Logger.getLogger(SocketDispatcher.class);
	private SocketDispatcher socketDispatcher;

	public ClientSessionHandler(SocketDispatcher socketDispatcher) {
		super();
		this.socketDispatcher = socketDispatcher;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		socketDispatcher.log.error(throwable, throwable);
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object object) throws Exception {
		processClientData(session, object);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		socketDispatcher.unRegisterClient(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// 判断允许加载的ip
		InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
		if (!(socketDispatcher.getTrustIpService().isTrustIp(address))) {
			System.out.println("ip 不在允许的范围" + session);
			socketDispatcher.log.info("ip 不在允许的范围" + session);
			session.close(true);
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		socketDispatcher.registerClient(session);
		InetSocketAddress address = (InetSocketAddress) session.getRemoteAddress();
		socketDispatcher.log.info("ip: " + address.getAddress().getHostAddress());
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
		session.close(true);
	}

	/** 处理client发来的数据 */
	public void processClientData(IoSession session, Object object) {
		Integer sessionId = (Integer) session.getAttribute(Constant.SESSIONID);
		if (sessionId == null) {
			session.close(true);
			log.error("sessionId is null,close session........");
			return;
		}
		IoBuffer buffer = (IoBuffer) object;
		buffer.order(ByteOrder.LITTLE_ENDIAN);// 设置小头在前 默认大头序
		short type = buffer.getShort(14);
		short subType = buffer.getShort(16);
		if (!socketDispatcher.checkProtocol(session, type, subType)) { // 协议检查
			log.error("协议检查不通过........");
			return;
		}
		if (type > 10000) {// 发送scence 服
			socketDispatcher.dispatchToScenceServer(sessionId, buffer);
		} else {// 发world 服
			if (type == Protocol.MAIN_ACCOUNT) {
				if (subType == Protocol.ACCOUNT_Heartbeat && (Boolean) session.getAttribute(Constant.LOGINMARK_KEY)) {// 回应客户端心跳协议
					SocketDispatcher.heartbeat.setServiceTime(System.currentTimeMillis());
					IoBuffer byteBuffer = IoBuffer.wrap(ProtocolManager.makeSegment(SocketDispatcher.heartbeat).getPacketByteArray());
					session.write(byteBuffer.duplicate());
				} else {
					socketDispatcher.dispatchToWorldServer(sessionId, buffer);
				}
			} else if ((Boolean) session.getAttribute(Constant.LOGINMARK_KEY)) {// 判断用户是否已经登录或者为登录协议
				socketDispatcher.dispatchToWorldServer(sessionId, buffer);
			} else {// 不是心跳，不是登录协议，并且用户未登录则断开socket连接
				log.info("用户未登录Kill Session LOGINMARK:" + session.getAttribute(Constant.LOGINMARK_KEY) + "---type:" + type + "---subtype:" + subType);
				session.close(true);
			}
		}
	}

}
