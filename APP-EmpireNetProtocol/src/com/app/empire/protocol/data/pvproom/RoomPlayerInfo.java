package com.app.empire.protocol.data.pvproom;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 当前房间内的最新玩家数据列表
 * 
 * @author doter
 * 
 */
public class RoomPlayerInfo extends AbstractData {
	private int[] playerId;// 玩家角色id
	private String[] nickName;// 玩家昵称
	private int[] heroId;// 英雄流水id
	private int[] heroExtId;// 英雄配置id

	public RoomPlayerInfo(int sessionId, int serial) {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_RoomPlayerInfo, sessionId, serial);
	}
	public RoomPlayerInfo() {
		super(Protocol.MAIN_PVPROOM, Protocol.PVPROOM_RoomPlayerInfo);
	}

	public int[] getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int[] playerId) {
		this.playerId = playerId;
	}
	public String[] getNickName() {
		return nickName;
	}
	public void setNickName(String[] nickName) {
		this.nickName = nickName;
	}
	public int[] getHeroId() {
		return heroId;
	}
	public void setHeroId(int[] heroId) {
		this.heroId = heroId;
	}
	public int[] getHeroExtId() {
		return heroExtId;
	}
	public void setHeroExtId(int[] heroExtId) {
		this.heroExtId = heroExtId;
	}

}
