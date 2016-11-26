package com.app.empire.scene.util;

public class GatewayXml {
	private int id;
	private String remoteAddress;
	private String localAddress;
	private int port;
	private int castlePort;
	private int battlePort;
	private int chatPort;
	private int adminPort;

	GatewayXml(int id, String remoteAddress, String localAddress, int port, int cPort, int bPort, int chPort, int adminPort) {
		this.id = id;
		this.remoteAddress = remoteAddress;
		this.localAddress = localAddress;
		this.port = port;
		this.castlePort = cPort;
		this.battlePort = bPort;
		this.chatPort = chPort;
		this.adminPort = adminPort;
	}

	public int getId() {
		return id;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public int getCastlePort() {
		return castlePort;
	}

	public int getBattlePort() {
		return battlePort;
	}

	public int getChatPort() {
		return chatPort;
	}

	public int getPort() {
		return port;
	}

	public int getAdminPort() {
		return adminPort;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * 是否接受网关连接
	 * 
	 * @param remoteAddress
	 * @param remotePort
	 * @return
	 */
	public boolean accept(String remoteAddress, int remotePort) {
		if (remoteAddress.startsWith("/")) {
			remoteAddress = remoteAddress.substring(1);
		}
		
		if (localAddress.equals(remoteAddress) && (remotePort == castlePort || remotePort == battlePort || remotePort == chatPort)) {
			return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("GatewayXml:");
		sb.append("id=").append(id).append("|");
		sb.append("localAddress=").append(localAddress).append("|");
		sb.append("castlePort=").append(castlePort).append("|");
		sb.append("battlePort=").append(battlePort).append("|");
		sb.append("chatPort=").append(chatPort).append("|");
		return sb.toString();
	}
}