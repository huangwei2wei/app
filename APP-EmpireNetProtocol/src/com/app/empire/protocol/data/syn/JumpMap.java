package com.app.empire.protocol.data.syn;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 跳地图
 * 
 * @author doter
 */
public class JumpMap extends AbstractData {
	private int mapId;// 地图id
	private int width;// 落地宽度位置
	private int high;// 落地高度位置

	public JumpMap(int sessionId, int serial) {
		super(Protocol.MAIN_SYN, Protocol.SYN_JumpMap, sessionId, serial);
	}
	public JumpMap() {
		super(Protocol.MAIN_SYN, Protocol.SYN_JumpMap);
	}
	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHigh() {
		return high;
	}
	public void setHigh(int high) {
		this.high = high;
	}

}
