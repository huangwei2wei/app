package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 登录失败
 * @see AbstractData
 * @author mazheng
 */
public class LoginFail extends AbstractData {
    private String message;
    public LoginFail(int sessionId, int serial) {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_LoginFail, sessionId, serial);
    }

    public LoginFail() {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_LoginFail);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
