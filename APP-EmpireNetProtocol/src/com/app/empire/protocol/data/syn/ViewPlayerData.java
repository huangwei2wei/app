package com.app.empire.protocol.data.syn;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 推送玩家数据给视野范围内的玩家
 * 
 * @author doter
 */
public class ViewPlayerData extends AbstractData {

	private int[] playerId;// 玩家id
	private byte[] direction;// 方向1-12
	private int[] width;// 所在宽度位置
	private int[] high;// 所在高度位置
	private String[] nickname;// 昵称
	private int[] heroBaseId;// 英雄基础id

	public ViewPlayerData(int sessionId, int serial) {
		super(Protocol.MAIN_SYN, Protocol.SYN_ViewPlayerData, sessionId, serial);
	}
	public ViewPlayerData() {
		super(Protocol.MAIN_SYN, Protocol.SYN_ViewPlayerData);
	}
	public int[] getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int[] playerId) {
		this.playerId = playerId;
	}
	public byte[] getDirection() {
		return direction;
	}
	public void setDirection(byte[] direction) {
		this.direction = direction;
	}
	public int[] getWidth() {
		return width;
	}
	public void setWidth(int[] width) {
		this.width = width;
	}
	public int[] getHigh() {
		return high;
	}
	public void setHigh(int[] high) {
		this.high = high;
	}
	public String[] getNickname() {
		return nickname;
	}
	public void setNickname(String[] nickname) {
		this.nickname = nickname;
	}
	public int[] getHeroBaseId() {
		return heroBaseId;
	}
	public void setHeroBaseId(int[] heroBaseId) {
		this.heroBaseId = heroBaseId;
	}

}
