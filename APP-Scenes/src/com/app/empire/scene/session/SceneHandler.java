package com.app.empire.scene.session;

import org.apache.mina.core.session.IoSession;

import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.exec.HandlerAction;
import com.app.empire.scene.util.exec.ThreadManager;
import com.app.net.ProtocolFactory;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.handler.IDataHandler;
import com.app.session.SessionHandler;
import com.app.session.SessionRegistry;

/**
 * 类 SessionHandler Session处理类 继承SessionHandler ，由子类负责创建不同类型的Session类<br>
 * 抽象Session处理方法，实现基本处理逻辑
 * 
 * @since JDK 1.6
 */
public abstract class SceneHandler extends SessionHandler {

	/**
	 * 构造函数，初始化<tt>SessionRegistry</tt>值
	 * 
	 * @param registry
	 */
	public SceneHandler(SessionRegistry registry) {
		super(registry);
	}

	/*
	 * 处理dis 转发过来的消息
	 */
	@Override
	public void messageReceived(IoSession ioSession, Object msg) throws Exception {
		if (msg instanceof PbAbstractData) {
			PbAbstractData data = (PbAbstractData) msg;
			ConnectSession session = (ConnectSession) registry.getSession(ioSession);
			data.setHandlerSource(session);
			int playerId = data.getSessionId();
			if (playerId > 0) {
				ArmyProxy army = session.getPlayerService().getArmy(playerId);
				army.enqueue(new HandlerAction(army, data));
			} else {
				ThreadManager.cmdExecutor.enDefaultQueue(new HandlerAction(ThreadManager.cmdExecutor.getDefaultQueue(), data));
				// HandlerAction action = new HandlerAction(data);
				// action.start();
			}
			System.out.println("收到：" + msg);
		} else {
			System.out.println("-----------收到：" + msg);
		}

	}
}
