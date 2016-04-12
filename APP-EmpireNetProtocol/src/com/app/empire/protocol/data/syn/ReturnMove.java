package com.app.empire.protocol.data.syn;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/***
 * 广播玩家移动
 * 
 * @author doter
 */

public class ReturnMove extends AbstractData {
	private int playerId;// 玩家id
	private int id;// 玩家单位id（如英雄流水id
	private byte direction;// 方向1-12
	private int x;// 现在位置
	private int y;// 现在位置
	private int toX;// 目标位置
	private int toY;// 目标位置

	public ReturnMove(int sessionId, int serial) {
		super(Protocol.MAIN_SYN, Protocol.SYN_ReturnMove, sessionId, serial);
	}
	public ReturnMove() {
		super(Protocol.MAIN_SYN, Protocol.SYN_ReturnMove);
	}

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public byte getDirection() {
		return direction;
	}
	public void setDirection(byte direction) {
		this.direction = direction;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getToX() {
		return toX;
	}
	public void setToX(int toX) {
		this.toX = toX;
	}
	public int getToY() {
		return toY;
	}
	public void setToY(int toY) {
		this.toY = toY;
	}

}
