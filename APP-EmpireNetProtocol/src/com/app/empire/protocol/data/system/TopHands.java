package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class TopHands extends AbstractData {

    public TopHands(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_TopHands, sessionId, serial);
    }

    public TopHands() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_TopHands);
    }
}
