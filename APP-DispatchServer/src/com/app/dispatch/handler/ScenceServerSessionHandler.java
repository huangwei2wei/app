package com.app.dispatch.handler;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.app.dispatch.SocketDispatcher;
import com.app.dispatch.data.Packet;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.s2s.S2SSegment;

public class ScenceServerSessionHandler extends IoHandlerAdapter {
	private SocketDispatcher socketDispatcher;

	public ScenceServerSessionHandler(SocketDispatcher socketDispatcher) {
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
			if (packet.getTarget() == EnumTarget.WORLDSERVER.getValue()) {
				socketDispatcher.dispatchToWorldServer(packet.sessionId, packet.buffer);
			} else {
				// System.out.println("dis收到Scence数据发前端：" + packet.data.toString());
				socketDispatcher.dispatchToClient(packet);
			}
		} else {
			System.out.println(System.currentTimeMillis() + " dis收到Scence数据发系统：" + packet.data.toString());
			socketDispatcher.processControl(packet);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		socketDispatcher.setSceneServerSession(null);
		// 断线重连worldServer
		try {
			Thread.sleep(5000L);
			socketDispatcher.connectSceneServer();
			socketDispatcher.log.info("Scene Server 断线重连。。。");
		} catch (Exception e) {
			socketDispatcher.log.error(e.getMessage());
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		socketDispatcher.setSceneServerSession(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
		S2SSegment seg = new S2SSegment(Protocol.MAIN_SERVER, Protocol.SERVER_Heartbeat);
		socketDispatcher.dispatchToScenceServer(-1, IoBuffer.wrap(seg.getPacketByteArray()));
		socketDispatcher.sendControlSegment(seg);
	}

	@Override
	public void sessionOpened(IoSession session) {
	}
}