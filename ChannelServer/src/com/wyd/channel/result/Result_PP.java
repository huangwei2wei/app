package com.wyd.channel.result;

public class Result_PP {
	public static int MiniObjectSize = 3*4;
	private int len;
	private int command;
	private int status;
	private String username;
	private long userid;

	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	
}

