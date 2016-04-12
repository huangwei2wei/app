package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetRoleList extends AbstractData {
    public GetRoleList(int sessionId, int serial) {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_GetRoleList, sessionId, serial);
    }

    public GetRoleList() {
        super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_GetRoleList);
    }
}
