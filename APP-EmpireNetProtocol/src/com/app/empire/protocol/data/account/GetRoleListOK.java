package com.app.empire.protocol.data.account;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetRoleListOK extends AbstractData {
	private int playerCount; // 角色数量
	private String[] nickName; // 角色名称
	private int[] gold; // 金币
	private int[] lv; // 玩家等级
	private int[] lvExp; // 经验
	private int[] vipExp; // vip经验
	private int[] vipLv; // vip等级
	private String[] property; // 属性
	private int[] fight; // 战斗力

	public GetRoleListOK(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_GetRoleListOK, sessionId, serial);
	}

	public GetRoleListOK() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_GetRoleListOK);
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public String[] getNickName() {
		return nickName;
	}

	public void setNickName(String[] nickName) {
		this.nickName = nickName;
	}

	public int[] getLv() {
		return lv;
	}

	public void setLv(int[] lv) {
		this.lv = lv;
	}

	public int[] getLvExp() {
		return lvExp;
	}

	public void setLvExp(int[] lvExp) {
		this.lvExp = lvExp;
	}

	public int[] getGold() {
		return gold;
	}

	public void setGold(int[] gold) {
		this.gold = gold;
	}

	public int[] getVipExp() {
		return vipExp;
	}

	public void setVipExp(int[] vipExp) {
		this.vipExp = vipExp;
	}

	public int[] getVipLv() {
		return vipLv;
	}

	public void setVipLv(int[] vipLv) {
		this.vipLv = vipLv;
	}

	public String[] getProperty() {
		return property;
	}

	public void setProperty(String[] property) {
		this.property = property;
	}

	public int[] getFight() {
		return fight;
	}

	public void setFight(int[] fight) {
		this.fight = fight;
	}

}
