package com.app.empire.protocol.data.shop;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;


public class BuyOk extends AbstractData {
	public BuyOk(int sessionId, int serial) {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_BuyOk, sessionId, serial);
	}
	public BuyOk() {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_BuyOk);
	}
}
