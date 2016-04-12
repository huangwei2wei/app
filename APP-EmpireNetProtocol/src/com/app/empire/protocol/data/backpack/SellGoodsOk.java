package com.app.empire.protocol.data.backpack;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class SellGoodsOk extends AbstractData {
	private String name;// 出售获得资源的 名称 如：gold金币，diamond钻石
	private int price; // 出售价格

	public SellGoodsOk(int sessionId, int serial) {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_SellGoodsOk, sessionId, serial);
	}
	public SellGoodsOk() {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_SellGoodsOk);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}

}
