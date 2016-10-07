package com.app.empire.scene.session;

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

//	/*
//	 * 处理dis 转发过来的消息
//	 */
//	@Override
//	public void messageReceived(IoSession ioSession, Object msg) throws Exception {
//		AbstractData dataobj = (AbstractData) msg;
//		Session session = this.registry.getSession(ioSession);
//		if (session != null)
//			ServiceManager.getManager().getAbstractService().addAbstractInfo(dataobj, session);
//		else
//			System.out.println(dataobj.toString() + "<<<<<<<<<<<<<<<<<<<");
//	}
}
