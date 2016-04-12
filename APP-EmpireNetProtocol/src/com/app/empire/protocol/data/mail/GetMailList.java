package com.app.empire.protocol.data.mail;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetMailList extends AbstractData {
	private int skip;// 起始记录下标，0开始
	private int limit;// 返回记录条数

	public GetMailList(int sessionId, int serial) {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_GetMailList, sessionId, serial);
	}
	public GetMailList() {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_GetMailList);
	}
	public int getSkip() {
		return skip;
	}
	public void setSkip(int skip) {
		this.skip = skip;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}

}
