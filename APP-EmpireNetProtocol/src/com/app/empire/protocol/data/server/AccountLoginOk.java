package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class AccountLoginOk extends AbstractData {
	private int accountId;
	private String name;
	private String password;
	private int tokenAmount;
	private int channel; // 渠道ID
	private int status; // 登录结果 0成功，1用户名或密码错误，2系统异常

	public AccountLoginOk(int sessionId, int serial) {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_AccountLoginOk, sessionId, serial);
	}

	public AccountLoginOk() {
		super(Protocol.MAIN_SERVER, Protocol.SERVER_AccountLoginOk);
	}

	public int getAccountId() {
		return this.accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String accountName) {
		this.name = accountName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setTokenAmount(int tokenAmount) {
		this.tokenAmount = tokenAmount;
	}

	public int getTokenAmount() {
		return tokenAmount;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}