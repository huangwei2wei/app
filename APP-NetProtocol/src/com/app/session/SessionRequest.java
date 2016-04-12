package com.app.session;
public class SessionRequest extends AbstractRequest {
    protected String sessionId;

    public SessionRequest(int id, int type, String sessionId) {
        super(id, type);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}
