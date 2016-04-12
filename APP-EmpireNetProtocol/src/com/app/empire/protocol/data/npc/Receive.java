package com.app.empire.protocol.data.npc;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 领取
 * 
 * @author doter
 * 
 */
public class Receive extends AbstractData {
	private int npcType;// 1 购买金币，2 购买粮草
	public Receive(int sessionId, int serial) {
		super(Protocol.MAIN_NPC, Protocol.NPC_Receive, sessionId, serial);
	}

	public Receive() {
		super(Protocol.MAIN_NPC, Protocol.NPC_Receive);
	}

	public int getNpcType() {
		return npcType;
	}

	public void setNpcType(int npcType) {
		this.npcType = npcType;
	}

}
