package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 类 <code>LoginOk</code>继承抽象类<code>AbstractData</code>，实现接口主命令Protocol.MAIN_ACCOUNT下子命令ACCOUNT_LoginOk(账户登录成功)对应数据封装。
 * 
 * @see AbstractData
 * @author doter
 */
public class LoginOk extends AbstractData {
    public LoginOk(int sessionId, int serial) {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_LoginOk, sessionId, serial);
    }

    public LoginOk() {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_LoginOk);
    }
}
