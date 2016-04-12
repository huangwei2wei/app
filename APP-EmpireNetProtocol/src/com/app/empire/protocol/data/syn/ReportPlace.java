package com.app.empire.protocol.data.syn;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/***
 * 告知服务器玩家位置
 * 
 * @author doter
 */

public class ReportPlace extends AbstractData {
	private int heroId;// 英雄流水id
	private byte direction;// 方向1-12
	private int x;// 所在位置
	private int y;// 所在位置

	public ReportPlace(int sessionId, int serial) {
		super(Protocol.MAIN_SYN, Protocol.SYN_ReportPlace, sessionId, serial);
	}
	public ReportPlace() {
		super(Protocol.MAIN_SYN, Protocol.SYN_ReportPlace);
	}

	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
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

}
