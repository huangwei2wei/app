package com.app.empire.protocol.data.npc;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class BuyOK extends AbstractData {
	private int npcType;// 1金币2粮草
	private int value;// 获得的数量

	public BuyOK(int sessionId, int serial) {
		super(Protocol.MAIN_NPC, Protocol.NPC_BuyOK, sessionId, serial);
	}

	public BuyOK() {
		super(Protocol.MAIN_NPC, Protocol.NPC_BuyOK);
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
