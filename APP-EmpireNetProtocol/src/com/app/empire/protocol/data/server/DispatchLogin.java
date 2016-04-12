package com.app.empire.protocol.data.server;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class DispatchLogin extends AbstractData {
    private String id;
    private String password;
    private int    maxPlayer;

    public DispatchLogin(int sessionId, int serial) {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_DispatchLogin, sessionId, serial);
    }

    public DispatchLogin() {
        super(Protocol.MAIN_SERVER, Protocol.SERVER_DispatchLogin);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxPlayer() {
        return this.maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }
}
