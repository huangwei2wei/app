package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class RoleLogin extends AbstractData {
	private String nickname;
	private int heroExtId;
	private String clientModel;// 手机型号
	private String systemName;// 手机系统
	private String systemVersion;// 系统版本

	public RoleLogin(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RoleLogin, sessionId, serial);
	}

	public RoleLogin() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RoleLogin);
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public int getHeroExtId() {
		return heroExtId;
	}

	public void setHeroExtId(int heroExtId) {
		this.heroExtId = heroExtId;
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
