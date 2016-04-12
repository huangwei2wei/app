package com.app.empire.world.request;

import com.app.empire.world.session.ConnectSession;

public class LoginRequest extends SessionRequest {
	protected String version;
	protected String name;
	protected String password;
	protected boolean isRelogin;
	protected String playerName;
	protected int channel;

	public LoginRequest(int id, int sessionId, ConnectSession session, String name, String password, String version, int channel,
			boolean isRelogin, String playerName) {
		super(IRequestType.LOGIN, id, sessionId, session);
		this.version = version;
		this.channel = channel;
		this.name = name;
		this.password = password;
		this.isRelogin = isRelogin;
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public boolean isReglogin() {
		return this.isRelogin;
	}

	public String getName() {
		return this.name;
	}

	public String getPassword() {
		return this.password;
	}

	public String getVersion() {
		return this.version;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}
}