package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class EarthPush extends AbstractData {
    private String showUrl;
    private String remark;

    public EarthPush(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_EarthPush, sessionId, serial);
    }

    public EarthPush() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_EarthPush);
    }

    public String getShowUrl() {
        return showUrl;
    }

    public void setShowUrl(String showUrl) {
        this.showUrl = showUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
