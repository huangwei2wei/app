package com.app.empire.protocol.data.npc;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 获取 npc
 * 
 * @author doter
 * 
 */
public class GetNpc extends AbstractData {

	public GetNpc(int sessionId, int serial) {
		super(Protocol.MAIN_NPC, Protocol.NPC_GetNpc, sessionId, serial);
	}

	public GetNpc() {
		super(Protocol.MAIN_NPC, Protocol.NPC_GetNpc);
	}

}
