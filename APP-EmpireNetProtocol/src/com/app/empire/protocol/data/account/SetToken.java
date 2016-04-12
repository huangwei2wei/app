package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 类 <code>Login</code>继承抽象类<code>AbstractData</code>，实现接口主命令Protocol.MAIN_ACCOUNT下子命令ACCOUNT_Login(账户登录)对应数据封装。
 * 
 * @see AbstractData
 * @author mazheng
 */
public class SetToken extends AbstractData {
	private String 	udid;
	private String 	token;
	
    public SetToken(int sessionId, int serial) {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_SetToken, sessionId, serial);
    }

    public SetToken() {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_SetToken);
    }

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
