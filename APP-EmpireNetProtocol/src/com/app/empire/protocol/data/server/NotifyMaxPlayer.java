package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 通知服务器最大在线人数。
 * 
 * @see AbstractData
 * @author mazheng
 */
public class NotifyMaxPlayer extends AbstractData {
    private int  currentCount;
    private int  maxCount;
    private long currentTime;

    public NotifyMaxPlayer(int sessionId, int serial) {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_NotifyMaxPlayer, sessionId, serial);
    }

    public NotifyMaxPlayer() {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_NotifyMaxPlayer);
    }

    public int getCurrentCount() {
        return this.currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public long getCurrentTime() {
        return this.currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
