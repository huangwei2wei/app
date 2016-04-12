package com.app.empire.protocol.data.purchase;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class BuySuccess extends AbstractData {
	private String orderNum;
	private int tisket;
	private int vipLv;//当前vip等级
	private int needTicket;//下一级需要钻石
	private int vipExp;// 当前vip 经验
	private int addTicket;//充值获得钻石
	private int giftTicket;//赠送的钻石
	
	public BuySuccess(int sessionId, int serial) {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_BuySuccess, sessionId, serial);
	}

	public BuySuccess() {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_BuySuccess);
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public int getTisket() {
		return tisket;
	}

	public void setTisket(int tisket) {
		this.tisket = tisket;
	}

	public int getVipLv() {
		return vipLv;
	}

	public void setVipLv(int vipLv) {
		this.vipLv = vipLv;
	}

	public int getNeedTicket() {
		return needTicket;
	}

	public void setNeedTicket(int needTicket) {
		this.needTicket = needTicket;
	}

	public int getVipExp() {
		return vipExp;
	}

	public void setVipExp(int vipExp) {
		this.vipExp = vipExp;
	}

	public int getAddTicket() {
		return addTicket;
	}

	public void setAddTicket(int addTicket) {
		this.addTicket = addTicket;
	}

	public int getGiftTicket() {
		return giftTicket;
	}

	public void setGiftTicket(int giftTicket) {
		this.giftTicket = giftTicket;
	}
	
	
	
}
