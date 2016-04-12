package com.app.empire.protocol.data.account;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 修改玩家角色名
 * 
 * @author doter
 * 
 */
public class UpdatePlayerName extends AbstractData {
	private String nickname; // 玩家角色名称

	public UpdatePlayerName(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_UpdatePlayerName, sessionId, serial);
	}
	public UpdatePlayerName() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_UpdatePlayerName);
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
