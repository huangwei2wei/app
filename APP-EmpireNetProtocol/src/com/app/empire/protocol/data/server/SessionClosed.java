package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class SessionClosed extends AbstractData {
    private int session;

    public SessionClosed(int sessionId, int serial) {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_SessionClosed, sessionId, serial);
    }

    public SessionClosed() {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_SessionClosed);
    }

    public int getSession() {
        return this.session;
    }

    public void setSession(int session) {
        this.session = session;
    }
}
