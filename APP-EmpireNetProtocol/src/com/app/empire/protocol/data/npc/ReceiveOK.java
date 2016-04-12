package com.app.empire.protocol.data.npc;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class ReceiveOK extends AbstractData {
	private int npcType;// 1 购买金币，2 购买粮草
	private int value;// 获得的数量

	public ReceiveOK(int sessionId, int serial) {
		super(Protocol.MAIN_NPC, Protocol.NPC_ReceiveOK, sessionId, serial);
	}

	public ReceiveOK() {
		super(Protocol.MAIN_NPC, Protocol.NPC_ReceiveOK);
	}

	public int getNpcType() {
		return npcType;
	}

	public void setNpcType(int npcType) {
		this.npcType = npcType;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
