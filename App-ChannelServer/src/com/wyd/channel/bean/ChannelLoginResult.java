package com.wyd.channel.bean;

public class ChannelLoginResult {
	private String code;
	private String message;

	public ChannelLoginResult() {

	}

	public ChannelLoginResult(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
