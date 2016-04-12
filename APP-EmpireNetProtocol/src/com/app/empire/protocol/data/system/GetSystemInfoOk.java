package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetSystemInfoOk extends AbstractData {
    private String rechargeUrl;//充值请求地址
    public GetSystemInfoOk(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetSystemInfoOk, sessionId, serial);
    }

    public GetSystemInfoOk() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetSystemInfoOk);
    }

    public String getRechargeUrl() {
        return rechargeUrl;
    }

    public void setRechargeUrl(String rechargeUrl) {
        this.rechargeUrl = rechargeUrl;
    }
}
