package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * (账户登录)对应数据封装。
 * 
 * @see AbstractData
 * @author doter
 */
public class Login extends AbstractData {
	private String accountName;// 账号名称
	private String passWord;// 密码
	private String version; // 客户端版本
	private int channel; // 渠道号
	private String clientModel;// 手机型号
	private String systemName;// 手机系统
	private String systemVersion;// 系统版本

	public Login(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_Login, sessionId, serial);
	}

	public Login() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_Login);
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
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

}
