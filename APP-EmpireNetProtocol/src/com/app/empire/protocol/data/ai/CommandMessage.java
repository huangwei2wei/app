package com.app.empire.protocol.data.ai;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class CommandMessage extends AbstractData {
	private String cmd;// 控制命令

	public CommandMessage(int sessionId, int serial) {
		super(Protocol.MAIN_AI, Protocol.CHAT_ReceiveMessage, sessionId, serial);
	}

	public CommandMessage() {
		super(Protocol.MAIN_AI, Protocol.CHAT_ReceiveMessage);
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

}
