package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class AccountLogin extends AbstractData {

	private String name;
	private String password;
	private int channel; // 渠道ID
	private String ip;
	private String clientModel;// 手机型号
	private String systemName;// 手机系统
	private String systemVersion;// 系统版本

	public AccountLogin(int sessionId, int serial) {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_AccountLogin, sessionId, serial);
	}

	public AccountLogin() {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_AccountLogin);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getClientModel() {
		return clientModel;
	}

	public void setClientModel(String clientModel) {
		this.clientModel = clientModel;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

}
