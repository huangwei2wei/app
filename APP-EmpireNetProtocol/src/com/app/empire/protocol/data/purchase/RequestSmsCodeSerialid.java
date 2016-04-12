package com.app.empire.protocol.data.purchase;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class RequestSmsCodeSerialid extends AbstractData {
    private int    id;
    private int    localId;
    private String channel;
    private String payChannel;

    public RequestSmsCodeSerialid(int sessionId, int serial) {
        super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_RequestSmsCodeSerialid, sessionId, serial);
    }

    public RequestSmsCodeSerialid() {
        super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_RequestSmsCodeSerialid);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }
}
