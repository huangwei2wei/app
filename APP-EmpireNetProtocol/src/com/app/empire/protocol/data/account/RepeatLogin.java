package com.app.empire.protocol.data.account;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 用户重复登录账号
 * @author doter
 *
 */
public class RepeatLogin extends AbstractData {
    private String message;
    
    public RepeatLogin(int sessionId, int serial) {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RepeatLogin, sessionId, serial);
    }

    public RepeatLogin() {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RepeatLogin);
    }

    public RepeatLogin(String message) {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RepeatLogin);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
