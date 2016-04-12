package com.app.dispatch;

import java.util.Vector;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 链接存储类
 * */
public class Channel {
	private String name;// 通道组名
	private Vector<IoSession> sessions = new Vector<IoSession>();// 通道组IoSession

	public Channel(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void join(IoSession session) {
		this.sessions.add(session);
	}
	/**
	 * 删除组内 ioSession
	 * 
	 * @param session
	 * @return 组内io数量
	 */
	public int removeSession(IoSession session) {
		this.sessions.remove(session);
		return this.sessions.size();
	}
	/** 广播组内所有玩家 */
	public void broadcast(IoBuffer buffer) {
		for (IoSession session : this.sessions)
			session.write(buffer.duplicate());
	}
}