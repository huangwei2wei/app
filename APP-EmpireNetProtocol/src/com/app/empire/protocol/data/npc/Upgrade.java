package com.app.empire.protocol.data.npc;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * NPC 升级
 * 
 * @author doter
 * 
 */
public class Upgrade extends AbstractData {
	private int npcType;// npc 类型1、金币npc，2、粮草npc

	public Upgrade(int sessionId, int serial) {
		super(Protocol.MAIN_NPC, Protocol.NPC_Upgrade, sessionId, serial);
	}

	public Upgrade() {
		super(Protocol.MAIN_NPC, Protocol.NPC_Upgrade);
	}

	public int getNpcType() {
		return npcType;
	}

	public void setNpcType(int npcType) {
		this.npcType = npcType;
	}

}
