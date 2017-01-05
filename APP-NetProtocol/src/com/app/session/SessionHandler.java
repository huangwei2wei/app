package com.app.session;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.app.net.ProtocolFactory;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 类 <code>SessionHandler</code> Session处理类<br>
 * 继承IoHandler，并提供createSession接口，由子类负责创建不同类型的Session类<br>
 * 抽象Session处理方法，实现基本处理逻辑
 * 
 * @since JDK 1.6
 */
public abstract class SessionHandler implements IoHandler {
	protected SessionRegistry registry = null;
	private Logger log = Logger.getLogger(SessionHandler.class);

	// SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSSS");
	/**
	 * 构造函数，初始化<tt>SessionRegistry</tt>值
	 * 
	 * @param registry
	 */
	public SessionHandler(SessionRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable ex) throws Exception {
		// session.close(true);
		log.error(ex, ex);
	}

	@Override
	public void messageReceived(IoSession ioSession, Object msg) throws Exception {
		AbstractData dataobj = (AbstractData) msg;
		Session session = this.registry.getSession(ioSession);
		if (session != null) {
			IDataHandler handler = ProtocolFactory.getDataHandler(dataobj);
			System.err.println("handler: " + handler + " session: " + session);
			if (handler == null) {
				session.handle(dataobj);
			} else {
				try {
					// 调试用记录日志
					// log.info("receive data class name:" +
					// dataobj.getClass().getName());.
					// System.out.println("receive data class name:" +
					// dataobj.getClass().getName());
					dataobj.setHandlerSource(session);
					// HandlerMonitorService.addMonitor(dataobj);
					handler.handle(dataobj);
					// HandlerMonitorService.delMonitor(dataobj);
					// if (abstractData != null)
					// session.write(abstractData);
				} catch (ProtocolException e) {
					HandlerMonitorService.delMonitor(dataobj);
					session.sendError(e);
				} catch (Exception e) {
					HandlerMonitorService.delMonitor(dataobj);
					e.printStackTrace();
					this.log.error("messageReceived-handle-error", e);
				}
			}
		}else{
			this.log.error("内部错误: session is null ");
		}
	}

	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Session s = this.registry.removeSession(session);
		if (s != null)
			s.closed();
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		Session s = createSession(session);
		if (s != null) {
			this.registry.registry(s);
			s.created();
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		Session s = this.registry.getSession(session);
		System.out.println(new Date() + " 链接空闲－－" + session.toString());
		if (s != null) {
			s.idle(session, status);
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		Session s = this.registry.getSession(session);
		if (s != null)
			s.opened();
	}

	public SessionRegistry getSessionRegistry() {
		return this.registry;
	}

	public abstract Session createSession(IoSession paramIoSession);
}