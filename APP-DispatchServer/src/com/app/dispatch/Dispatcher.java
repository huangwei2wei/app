package com.app.dispatch;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.app.protocol.INetData;
public abstract interface Dispatcher {
	public abstract void broadcast(IoBuffer paramByteBuffer);

	public abstract void unRegisterClient(int paramInt);

	public abstract IoSession getSession(int paramInt);

	public abstract void shutdown();
	
	public abstract void syncPlayer(INetData data);
}