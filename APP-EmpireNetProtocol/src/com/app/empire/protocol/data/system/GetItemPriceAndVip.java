package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetItemPriceAndVip extends AbstractData {

    public GetItemPriceAndVip(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetItemPriceAndVip, sessionId, serial);
    }

    public GetItemPriceAndVip() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetItemPriceAndVip);
    }
}
