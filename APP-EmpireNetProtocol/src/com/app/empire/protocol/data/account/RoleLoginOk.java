package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class RoleLoginOk extends AbstractData {
	private int id;// 角色id
	private int heroExtId;// 英雄id
	private String nickname; // 玩家角色名称
	private int lv; // 玩家等级
	private int lvExp; // 玩家等级
	private int vipLv; // 玩家vip等级
	private int vipExp; // 玩家vip经验
	private int fight; // 玩家当前战斗力
	private int diamond;// 钻石
	private int gold; // 玩家金币数量
	private int power;// 体力
	private String property;// 属性
	private String moduleUseInfo;// 模块使用情况（每天重置
	private String moduleBuyInfo;// 功能购买情况（每天重置

	public RoleLoginOk(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RoleLoginOk, sessionId, serial);
	}

	public RoleLoginOk() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RoleLoginOk);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHeroExtId() {
		return heroExtId;
	}

	public void setHeroExtId(int heroExtId) {
		this.heroExtId = heroExtId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public int getLvExp() {
		return lvExp;
	}

	public void setLvExp(int lvExp) {
		this.lvExp = lvExp;
	}

	public int getVipLv() {
		return vipLv;
	}

	public void setVipLv(int vipLv) {
		this.vipLv = vipLv;
	}

	public int getVipExp() {
		return vipExp;
	}

	public void setVipExp(int vipExp) {
		this.vipExp = vipExp;
	}

	public int getFight() {
		return fight;
	}

	public void setFight(int fight) {
		this.fight = fight;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getModuleUseInfo() {
		return moduleUseInfo;
	}

	public void setModuleUseInfo(String moduleUseInfo) {
		this.moduleUseInfo = moduleUseInfo;
	}

	public String getModuleBuyInfo() {
		return moduleBuyInfo;
	}

	public void setModuleBuyInfo(String moduleBuyInfo) {
		this.moduleBuyInfo = moduleBuyInfo;
	}

}
