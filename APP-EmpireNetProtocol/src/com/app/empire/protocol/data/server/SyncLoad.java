package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class SyncLoad extends AbstractData {
    private int     currOnline;
    private int     maxOnline;
    private boolean maintance = false;

    public SyncLoad(int sessionId, int serial) {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_SyncLoad, sessionId, serial);
    }

    public SyncLoad() {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_SyncLoad);
    }

    public int getCurrOnline() {
        return this.currOnline;
    }

    public void setCurrOnline(int currOnline) {
        this.currOnline = currOnline;
    }

    public int getMaxOnline() {
        return this.maxOnline;
    }

    public void setMaxOnline(int maxOnline) {
        this.maxOnline = maxOnline;
    }

    public boolean getMaintance() {
        return this.maintance;
    }

    public void setMaintance(boolean maintance) {
        this.maintance = maintance;
    }
}
