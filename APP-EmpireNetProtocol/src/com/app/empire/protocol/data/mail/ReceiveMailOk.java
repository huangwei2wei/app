package com.app.empire.protocol.data.mail;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class ReceiveMailOk extends AbstractData {
	private String goods;// 领取获得的物品 json格式

	public ReceiveMailOk(int sessionId, int serial) {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_ReceiveMailOk, sessionId, serial);
	}
	public ReceiveMailOk() {
		super(Protocol.MAIN_MAIL, Protocol.MAIL_ReceiveMailOk);
	}
	public String getGoods() {
		return goods;
	}
	public void setGoods(String goods) {
		this.goods = goods;
	}

}
