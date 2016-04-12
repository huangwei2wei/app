package com.app.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.IoSession;

/**
 * 类 SessionRegistry Session注册管理类 管理Session与IoSession，Id与Session关系
 * SessionRegistry会话注册类，用于快速查找IoSession与Session子类的映射关系
 * 
 * @since JDK 1.7
 */
public class SessionRegistry {
	private ConcurrentHashMap<IoSession, Session> ioSession2Session = new ConcurrentHashMap<IoSession, Session>();
	private ConcurrentHashMap<Integer, Session> sessionID2Session = new ConcurrentHashMap<Integer, Session>();
	private AtomicInteger i = new AtomicInteger(0);

	/**
	 * 注册会话session 将参数中 session</tt>的<tt>IoSession</tt>与<tt>session</tt>本身存入哈希表中<br>
	 * 将新的SessionId与数中<tt>session</tt>存入哈希表中
	 * 
	 * @param session
	 *            会话
	 */
	public void registry(Session session) {
		this.ioSession2Session.put(session.getIoSession(), session);
		int sessionId = i.incrementAndGet();
		session.sessionId = sessionId;
		this.sessionID2Session.put(sessionId, session);
	}

	/**
	 * 根据<tt>IoSession</tt>删除对应哈希表中对应数据
	 * 
	 * @param session
	 *            IoSession
	 * @return 被删掉哈希表中IoSession对应的<tt>session</tt>值
	 */
	public Session removeSession(IoSession session) {
		Session sessions = this.ioSession2Session.remove(session);
		if (sessions != null)
			this.sessionID2Session.remove(sessions.sessionId);
		return sessions;
	}

	/**
	 * 根据<tt>sessionId</tt>删除哈希表数据中对应<tt>Session</tt>值
	 * 
	 * @param sessionId
	 *            会话ID
	 */
	public Session removeSession(int sessionId) {
		Session sessions = this.sessionID2Session.remove(sessionId);
		if (sessions != null)
			this.sessionID2Session.remove(sessions.getIoSession());
		return sessions;
	}

	/**
	 * 根据<tt>IoSession</tt>返回哈希表数据中对应<tt>Session</tt>值
	 * 
	 * @param session
	 *            IoSession
	 * @return
	 */
	public Session getSession(IoSession session) {
		return this.ioSession2Session.get(session);
	}

	/**
	 * 根据<tt>sessionId</tt>返回哈希表数据中对应<tt>Session</tt>值
	 * 
	 * @param sessionId
	 *            会话ID
	 * @return
	 */
	public Session getSession(int sessionId) {
		return this.sessionID2Session.get(sessionId);
	}

	public ConcurrentHashMap<IoSession, Session> getIoSession2Session() {
		return ioSession2Session;
	}

	public ConcurrentHashMap<Integer, Session> getSessionID2Session() {
		return sessionID2Session;
	}
}