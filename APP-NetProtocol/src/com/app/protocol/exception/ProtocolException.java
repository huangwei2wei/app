package com.app.protocol.exception;

import com.app.protocol.data.AbstractData;

public class ProtocolException extends Exception {
    private static final long serialVersionUID = -6469638634722657818L;
    private int               serial;
    private int               sessionId;
    private byte              type;
    private byte              subType;

    public ProtocolException(String message, int serial, int sessionId, byte type, byte subType) {
        super(message);
        this.serial = serial;
        this.sessionId = sessionId;
        this.type = type;
        this.subType = subType;
    }

    public ProtocolException(String message, int serial, byte type, byte subType) {
        this(message, serial, -1, type, subType);
    }
    
    public ProtocolException(AbstractData data,String message) {
        this(message, data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
    }

    public int getSerial() {
        return this.serial;
    }

    public int getSessionId() {
        return this.sessionId;
    }

    public byte getType() {
        return this.type;
    }

    public byte getSubType() {
        return this.subType;
    }
}
