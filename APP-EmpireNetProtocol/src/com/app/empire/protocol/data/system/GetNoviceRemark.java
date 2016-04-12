package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetNoviceRemark extends AbstractData {

    public GetNoviceRemark(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetNoviceRemark, sessionId, serial);
    }

    public GetNoviceRemark() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetNoviceRemark);
    }
}
