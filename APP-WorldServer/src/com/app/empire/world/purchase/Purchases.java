package com.app.empire.world.purchase;

public class Purchases {
	private String orderNum;
	private String playerId;
	private String key;
	private int orderType;

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = Integer.parseInt(orderType);
	}
}
