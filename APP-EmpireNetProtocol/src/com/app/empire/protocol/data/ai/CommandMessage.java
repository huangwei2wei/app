package com.app.empire.protocol.data.ai;

import com.app.protocol.data.AbstractData;

public abstract class CommandMessage extends AbstractData {
	private String cmd;// 控制命令

	public CommandMessage(byte type, byte subType, int sessionId, int serial) {
		super(type, subType, sessionId, serial);
	}

	public CommandMessage(byte type, byte subType) {
		super(type, subType);
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

}
