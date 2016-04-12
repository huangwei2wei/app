package com.app.empire.protocol.data.shop;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 神秘商人
 * 
 * @author doter
 * 
 */
public class GetShop extends AbstractData {
	private int shopType;// 商店类型 1、神秘商店

	public GetShop(int sessionId, int serial) {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_GetShop, sessionId, serial);
	}
	public GetShop() {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_GetShop);
	}

	public int getShopType() {
		return shopType;
	}
	public void setShopType(int shopType) {
		this.shopType = shopType;
	}

}
