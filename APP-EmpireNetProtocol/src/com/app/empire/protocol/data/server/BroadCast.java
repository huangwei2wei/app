package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class BroadCast extends AbstractData {
    private String channel;//频道标识
    private byte[] data;

    public BroadCast(int sessionId, int serial) {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_BroadCast, sessionId, serial);
    }

    public BroadCast() {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_BroadCast);
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
