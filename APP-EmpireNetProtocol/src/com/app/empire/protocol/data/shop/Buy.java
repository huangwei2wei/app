package com.app.empire.protocol.data.shop;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 商店购买物品
 * 
 * @author doter
 * 
 */
public class Buy extends AbstractData {
	private int shopType;// 商店类型 1、神秘商店
	private int id;// 配置表id
	private int num;// 物品数量

	public Buy(int sessionId, int serial) {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_Buy, sessionId, serial);
	}
	public Buy() {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_Buy);
	}
	
	public int getShopType() {
		return shopType;
	}
	public void setShopType(int shopType) {
		this.shopType = shopType;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}

}
