package com.app.protocol.exception;

import com.app.protocol.data.AbstractData;

public class ProtocolException extends Exception {
    private static final long serialVersionUID = -6469638634722657818L;
    private int               serial;
    private int               sessionId;
    private short              type;
    private short              subType;

    public ProtocolException(String message, int serial, int sessionId, short type, short subType) {
        super(message);
        this.serial = serial;
        this.sessionId = sessionId;
        this.type = type;
        this.subType = subType;
    }

    public ProtocolException(String message, int serial, short type, short subType) {
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

    public short getType() {
        return this.type;
    }

    public short getSubType() {
        return this.subType;
    }
}
