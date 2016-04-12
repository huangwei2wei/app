package com.app.empire.protocol.data.purchase;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class RequestSmsCodeSerialidOk extends AbstractData {
    private String serialNum;
    private int    id;
    private int    localId;

    public RequestSmsCodeSerialidOk(int sessionId, int serial) {
        super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_RequestSmsCodeSerialidOk, sessionId, serial);
    }

    public RequestSmsCodeSerialidOk() {
        super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_RequestSmsCodeSerialidOk);
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
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
}
