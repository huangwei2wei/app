package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 类 <code>Login</code>继承抽象类<code>AbstractData</code>
 * ，实现接口主命令Protocol.MAIN_ACCOUNT下子命令ACCOUNT_Login(账户登录)对应数据封装。
 * 
 * @see AbstractData
 * @author doter
 */
public class LoginAgain extends AbstractData {
	private String accountName;// 账号名称
	private String version; // 客户端版本
	private int channel; // 渠道号
	private String clientModel;// 手机型号
	private String systemName;// 手机系统
	private String systemVersion;// 系统版本
	private String playerName;

	public LoginAgain(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_LoginAgain, sessionId, serial);
	}

	public LoginAgain() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_LoginAgain);
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
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

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

}
