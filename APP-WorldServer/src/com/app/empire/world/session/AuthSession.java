package com.app.empire.world.session;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.app.session.Session;

public class AuthSession extends Session {
	// private static final Logger log = Logger.getLogger(AuthSession.class);

	public AuthSession(IoSession session) {
		super(session);
	}

	@Override
	public <AbstractData> void handle(AbstractData packet) {
	}

	@Override
	public void created() {
	}

 

	@Override
	public void opened() {
	}

	@Override
	public void closed() {
	}

	@Override
	public void idle(IoSession session, IdleStatus status) {
		session.close(true);
	}
}