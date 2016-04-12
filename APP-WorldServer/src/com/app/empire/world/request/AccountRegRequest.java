package com.app.empire.world.request;

import com.app.empire.world.session.ConnectSession;

public class AccountRegRequest extends SessionRequest {
	public AccountRegRequest(int id, int sessionId, ConnectSession session) {
		super(IRequestType.ACCOUNT_REG, id, sessionId, session);
	}
}