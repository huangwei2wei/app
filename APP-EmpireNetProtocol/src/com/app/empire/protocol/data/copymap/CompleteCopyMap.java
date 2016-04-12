package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 完成副本
 * 
 * @author doter
 * 
 */
public class CompleteCopyMap extends AbstractData {
	private byte copyMapType;// 副本类型1：主线副本
	private int copyMapId;// 副本id
	private int star;// 星数

	public CompleteCopyMap(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_CompleteCopyMap, sessionId, serial);
	}

	public CompleteCopyMap() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_CompleteCopyMap);
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

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

}
