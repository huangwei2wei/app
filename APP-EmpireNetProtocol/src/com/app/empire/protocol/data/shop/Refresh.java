package com.app.empire.protocol.data.shop;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 刷新神秘商店
 * 
 * @author doter
 * 
 */
public class Refresh extends AbstractData {
	private int shopType;// 商店类型 1、神秘商店
	public Refresh(int sessionId, int serial) {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_Refresh, sessionId, serial);
	}
	public Refresh() {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_Refresh);
	}
	public int getShopType() {
		return shopType;
	}
	public void setShopType(int shopType) {
		this.shopType = shopType;
	}

}
