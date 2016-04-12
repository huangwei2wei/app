package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 类 <code>NotifyMaintance</code>继承抽象类<code>AbstractData</code>，实现接口主命令Protocol.MAIN_SERVER下子命令SERVER_NotifyMaintance(通知服务器维护状态)对应数据封装。
 * 
 * @see AbstractData
 * @author mazheng
 */
public class NotifyMaintance extends AbstractData {
    private boolean maintance;

    public NotifyMaintance(int sessionId, int serial) {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_NotifyMaintance, sessionId, serial);
    }

    public NotifyMaintance() {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_NotifyMaintance);
    }

    public boolean getMaintance() {
        return this.maintance;
    }

    public void setMaintance(boolean maintance) {
        this.maintance = maintance;
    }
}
