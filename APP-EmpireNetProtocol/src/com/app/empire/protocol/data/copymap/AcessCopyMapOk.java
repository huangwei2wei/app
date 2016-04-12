package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 领取副本成功
 * 
 * @author doter
 * 
 */
public class AcessCopyMapOk extends AbstractData {
	private int id;// 副本流水号

	public AcessCopyMapOk(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_AcessCopyMapOk, sessionId, serial);
	}
	public AcessCopyMapOk() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_AcessCopyMapOk);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
