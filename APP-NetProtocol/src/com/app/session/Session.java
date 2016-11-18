package com.app.session;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.app.protocol.data.AbstractData;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.exception.ProtocolException;
import com.google.protobuf.Message;

public abstract class Session {
	protected IoSession	session;
	protected int		sessionId;

	public Session(IoSession session) {
		this.session = session;
	}

	public Session() {
	}

	public abstract <T> void handle(T paramT);

	public abstract void closed();

	public abstract void created();

	public abstract void opened();

	public abstract void idle(IoSession session, IdleStatus status);

	public void defaultHandle() {
	}

	public IoSession getIoSession() {
		return this.session;
	}

	public void setIoSession(IoSession session) {
		this.session = session;
	}

	public boolean isConnected() {
		if (this.session == null) {
			return false;
		}
		return this.session.isConnected();
	}

	public void close() {
		if ((this.session != null) && (!(this.session.isClosing()))) {
			this.session.close(true);
		}
	}

	public void write(AbstractData data) {
		this.session.write(data);
	}

	public void write(short type, short subType, Message msg) {
		PbAbstractData pbMsg = new PbAbstractData(type, subType);
		pbMsg.setBytes(msg.toByteArray());
		write(pbMsg);
	}

	public void write(short type, short subType, int sessionId, int serial, Message msg) {
		PbAbstractData pbMsg = new PbAbstractData(type, subType, sessionId, serial);
		pbMsg.setBytes(msg.toByteArray());
		write(pbMsg);
	}

	public void reply(AbstractData data) {
		write(data);
	}

	public int getSessionId() {
		return this.sessionId;
	}

	public void forward(AbstractData data, int sessionId) {
		data.setSessionId(sessionId);
		write(data);
	}

	public void sendError(ProtocolException e) {
	}
}