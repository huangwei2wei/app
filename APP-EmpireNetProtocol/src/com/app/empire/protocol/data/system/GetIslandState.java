package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetIslandState extends AbstractData {

    public GetIslandState(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetIslandState, sessionId, serial);
    }

    public GetIslandState() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetIslandState);
    }
}
