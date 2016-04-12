package com.app.empire.protocol.data.server;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class WorldServerToAccountServer extends AbstractData{
	
	private String worldServerId;//worldServer 的标识

	public WorldServerToAccountServer(int sessionId, int serial) {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_WorldServerToAccountServer, sessionId, serial);
	}
	
	public WorldServerToAccountServer() {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_WorldServerToAccountServer);
	}

	public String getWorldServerId() {
		return worldServerId;
	}

	public void setWorldServerId(String worldServerId) {
		this.worldServerId = worldServerId;
	}

 
	
	
}
