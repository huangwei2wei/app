package com.app.dispatch.handler;

import org.apache.commons.configuration.Configuration;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.app.dispatch.SocketDispatcher;
import com.app.dispatch.data.Packet;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.s2s.S2SSegment;

/**
 * DispatchServer处理WorldServer数据信息,做为客户端的处理Handler，第一次登陆时注册dispatcher server信息
 */
public class WorldServerSessionHandler extends IoHandlerAdapter {
	private SocketDispatcher socketDispatcher;

	public WorldServerSessionHandler(SocketDispatcher socketDispatcher) {
		super();
		this.socketDispatcher = socketDispatcher;
	}

	@Override
	public void exceptionCaught(IoSession sesion, Throwable throwable) throws Exception {
		// sesion.close(true);
		socketDispatcher.log.error(throwable, throwable);
	}

	@Override
	public void messageReceived(IoSession session, Object object) throws Exception {
		Packet packet = (Packet) object;
		if (packet.type == Packet.TYPE.BUFFER) {
			// System.out.println("dis收到WORLD数据发前端或场景服：" + packet.data.toString());
			if (packet.getTarget() == EnumTarget.SCENESSERVER.getValue()) {
				socketDispatcher.dispatchToScenceServer(packet.buffer);
			} else {
				socketDispatcher.dispatchToClient(packet);
			}
		} else {
			System.out.println(System.currentTimeMillis() + "sessionId:" + packet.getSessionId() + " dis收到WORLD数据发系统：" + packet.data.toString());
			socketDispatcher.processControl(packet);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		socketDispatcher.setWorldServerSession(null);
		// worldServerSession = null;
		// 断线重连worldServer
		try {
			Thread.sleep(5000L);
			socketDispatcher.connectWorldServer();
			socketDispatcher.log.info("worldServer 断线重连。。。");
		} catch (Exception e) {
			socketDispatcher.log.error(e.getMessage());
		}
	}

	// I/O processor线程触发
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		socketDispatcher.setWorldServerSession(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
		S2SSegment seg = new S2SSegment(Protocol.MAIN_SERVER, Protocol.SERVER_Heartbeat);
		socketDispatcher.sendControlSegment(seg);
	}

	@Override
	public void sessionOpened(IoSession session) {
		S2SSegment seg = new S2SSegment(Protocol.MAIN_SERVER, Protocol.SERVER_DispatchLogin);
		Configuration configuration = socketDispatcher.getConfiguration();

		seg.writeString((String) socketDispatcher.getConfiguration().getProperty("area"));
		seg.writeString((String) configuration.getProperty("serverpassword"));
		seg.writeInt(configuration.getInt("maxplayer"));
		socketDispatcher.sendControlSegment(seg);
	}
}