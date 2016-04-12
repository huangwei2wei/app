package com.app.empire.protocol.data.syn;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/***
 * 玩家移动
 * 
 * @author doter
 * 
 */
public class Move extends AbstractData {
	private int id;// 玩家单位id（如英雄流水id
	private byte direction;// 朝向 1-12
	private int x;// 现在位置
	private int y;// 现在位置
	private int toX;// 目标位置
	private int toY;// 目标位置

	public Move(int sessionId, int serial) {
		super(Protocol.MAIN_SYN, Protocol.SYN_Move, sessionId, serial);
	}
	public Move() {
		super(Protocol.MAIN_SYN, Protocol.SYN_Move);
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
