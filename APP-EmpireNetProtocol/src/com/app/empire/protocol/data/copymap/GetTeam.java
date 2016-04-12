package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 获取战队
 * 
 * @author doter
 * 
 */

public class GetTeam extends AbstractData {
	private int teamType;// 类型1、主线副本

	public GetTeam(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_GetTeam, sessionId, serial);
	}
	public GetTeam() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_GetTeam);
	}
	public int getTeamType() {
		return teamType;
	}
	public void setTeamType(int teamType) {
		this.teamType = teamType;
	}

}
