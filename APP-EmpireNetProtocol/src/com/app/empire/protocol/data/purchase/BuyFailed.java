package com.app.empire.protocol.data.purchase;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class BuyFailed extends AbstractData {
	private String orderNum;
	private int code;
	
	public BuyFailed(int sessionId, int serial) {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_BuyFailed, sessionId, serial);
	}

	public BuyFailed() {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_BuyFailed);
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
