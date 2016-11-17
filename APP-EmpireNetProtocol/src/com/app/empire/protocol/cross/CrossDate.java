package com.app.empire.protocol.cross;

import com.app.protocol.data.AbstractData;

public abstract class CrossDate extends AbstractData {
	public CrossDate(byte type, byte subType) {
		super(type, subType, EnumTarget.WORLDSERVER.getValue());
	}

	public CrossDate(byte type, byte subType, int sessionId, int serial) {
		super(type, subType, sessionId, serial, EnumTarget.WORLDSERVER.getValue());
	}

	public abstract int getRoomId();

	public abstract void setRoomId(int roomId);
}
