package com.app.empire.world.model.exchange;

public class ExchangeResult {
	private int code; // 返回的代码 0成功，其他失败
	private String message; // 返回信息
	private int rewardType; // 奖励的类型 0游戏物品奖励，1邮件奖励

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getRewardType() {
		return rewardType;
	}

	public void setRewardType(int rewardType) {
		this.rewardType = rewardType;
	}
}
