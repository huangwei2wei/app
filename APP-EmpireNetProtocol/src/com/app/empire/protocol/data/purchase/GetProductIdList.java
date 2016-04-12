package com.app.empire.protocol.data.purchase;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetProductIdList extends AbstractData {
    private int channelId;

    public GetProductIdList(int sessionId, int serial) {
        super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_GetProductIdList, sessionId, serial);
    }

    public GetProductIdList() {
        super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_GetProductIdList);
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }
}
