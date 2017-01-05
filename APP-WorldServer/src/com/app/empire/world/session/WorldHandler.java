package com.app.empire.world.session;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.protocol.data.AbstractData;
import com.app.session.Session;
import com.app.session.SessionHandler;
import com.app.session.SessionRegistry;
import com.app.thread.exec.ThreadManager;

/**
 * 类 SessionHandler Session处理类 继承SessionHandler ，由子类负责创建不同类型的Session类<br>
 * 抽象Session处理方法，实现基本处理逻辑
 * 
 * @since JDK 1.6
 */
public class WorldHandler extends SessionHandler {
	private static Logger log = Logger.getLogger(WorldHandler.class);

	/**
	 * 构造函数，初始化<tt>SessionRegistry</tt>值
	 * 
	 * @param registry
	 */
	public WorldHandler(SessionRegistry registry) {
		super(registry);
	}

	@Override
	public Session createSession(IoSession session) {
		ConnectSession connSession = new ConnectSession(session);
		ServiceManager serviceManager = ServiceManager.getManager();
		connSession.setAccountSkeleton(serviceManager.getAccountSkeleton());
		connSession.setPlayerService(serviceManager.getPlayerService());
		return connSession;
	}

	/*
	 * 处理dis 转发过来的消息
	 */
	@Override
	public void messageReceived(IoSession ioSession, Object msg) throws Exception {
		Session session = this.registry.getSession(ioSession);
		AbstractData data = (AbstractData) msg;
		data.setHandlerSource(session);
		// if (msg instanceof AbstractData) {
		// if (session != null) {
		// ServiceManager.getManager().getAbstractService().addAbstractInfo(data, session);
		// } else {
		// System.out.println(data.toString() + "<<<<<<<<<<<<<<<<<<<");
		// }
		// } else if (msg instanceof PbAbstractData) {
		// }

		if (session != null) {
			int sessionId = data.getSessionId();
			if (sessionId > 0) {
				WorldPlayer worldPlayer = ((ConnectSession) session).getPlayer(sessionId);
				worldPlayer.enqueue(new HandlerAction(worldPlayer, data));
			} else {
				ThreadManager.cmdExecutor.enDefaultQueue(new HandlerAction(ThreadManager.cmdExecutor.getDefaultQueue(), data));
			}
		} else {
			log.error(data.toString() + "session is null ....");
			System.out.println(data.toString() + "session is null ....");
		}

	}
}
