package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class ShutDown extends AbstractData {
    public ShutDown(int sessionId, int serial) {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_ShutDown, sessionId, serial);
    }

    public ShutDown() {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_ShutDown);
    }
}
