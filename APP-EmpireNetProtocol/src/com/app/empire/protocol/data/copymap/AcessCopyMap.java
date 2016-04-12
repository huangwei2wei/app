package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 领取副本
 * 
 * @author doter
 * 
 */
public class AcessCopyMap extends AbstractData {
	private byte copyMapType;// 副本类型 1：主线副本
	private int copyMapId;// 副本id

	public AcessCopyMap(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_AcessCopyMap, sessionId, serial);
	}
	public AcessCopyMap() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_AcessCopyMap);
	}

	public byte getCopyMapType() {
		return copyMapType;
	}
	public void setCopyMapType(byte copyMapType) {
		this.copyMapType = copyMapType;
	}
	public int getCopyMapId() {
		return copyMapId;
	}
	public void setCopyMapId(int copyMapId) {
		this.copyMapId = copyMapId;
	}

}
