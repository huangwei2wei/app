package com.app.empire.world.model;

public class Client {
	private int sessionId = -1;// session id
	private int accountId = -1;// 账号id
	private int playerId = -1;// 角色id
	private String name;// 账号名称
	private String udid;
	private String password;
	private int flg;
	private STATUS status = STATUS.INIT;
	private int channel;
	private String uin;
	private String ip;// 客户端IP

	public Client(int sessionId) {
		this.sessionId = sessionId;
	}

	public boolean isLogin() {
		return ((this.status == STATUS.ACCOUNTLOGIN) || (this.status == STATUS.PLAYERLOGIN) || (this.status == STATUS.CREATEPLAYE));
	}

	/**
	 * 判断用户是否已经登录
	 * 
	 * @return <tt>true</tt> 已经登录<br>
	 *         <tt>false</tt> 没有登录
	 */
	public boolean isPlayerLogin() {
		return (this.status == STATUS.PLAYERLOGIN);
	}

	/**
	 * 折扣比例
	 * 
	 * @return
	 */
	public int getDiscount() {
		return 100;
	}

	public static enum STATUS {
		INIT, ACCOUNTLOGIN, PLAYERLOGIN, LOGOUT, CREATEPLAYE;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getFlg() {
		return flg;
	}

	public void setFlg(int flg) {
		this.flg = flg;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}