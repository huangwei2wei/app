package com.app.empire.protocol.cross;
import com.app.protocol.data.AbstractData;
public abstract class CrossDate extends AbstractData {
    public CrossDate(byte type, byte subType) {
        super(type, subType);
    }

    public CrossDate(byte type, byte subType, int sessionId, int serial) {
        super(type, subType, sessionId, serial);
    }

    public abstract int getRoomId();

    public abstract void setRoomId(int roomId);
}
