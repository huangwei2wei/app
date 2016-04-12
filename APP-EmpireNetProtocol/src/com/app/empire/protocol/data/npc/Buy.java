package com.app.empire.protocol.data.npc;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * npc 购买金币、粮草
 * 
 * @author doter
 * 
 */
public class Buy extends AbstractData {
	private int npcType;// 1 购买金币，2 购买粮草
	public Buy(int sessionId, int serial) {
		super(Protocol.MAIN_NPC, Protocol.NPC_Buy, sessionId, serial);
	}

	public Buy() {
		super(Protocol.MAIN_NPC, Protocol.NPC_Buy);
	}

	public int getNpcType() {
		return npcType;
	}

	public void setNpcType(int npcType) {
		this.npcType = npcType;
	}

}
