package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 完成副本
 * 
 * @author doter
 * 
 */
public class CompleteCopyMapOk extends AbstractData {
	private int id;// 副本流水号
	private String award;// 奖励物品

	public CompleteCopyMapOk(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_CompleteCopyMapOk, sessionId, serial);
	}

	public CompleteCopyMapOk() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_CompleteCopyMapOk);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAward() {
		return award;
	}

	public void setAward(String award) {
		this.award = award;
	}

}
