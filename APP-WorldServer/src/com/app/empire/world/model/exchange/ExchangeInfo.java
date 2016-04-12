package com.app.empire.world.model.exchange;

public class ExchangeInfo {
	private int playerId; // 兑换玩家id
	private String serviceId; // 兑换服务器id
	private String code; // 兑换码
	private int level; // 玩家的等级
	private int channel; // 玩家的渠道号

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}
}
