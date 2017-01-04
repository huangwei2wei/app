package com.app.empire.world.entity.mongo;

import java.util.Date;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.app.db.mongo.entity.IEntity;
import com.app.db.mongo.entity.Option;

/**
 * 角色表
 * 
 * @author doter
 */

@Document(collection = "player")
public class Player extends IEntity {
	private int accountId; // 玩家帐号ID
	private String accountName; // 玩家帐号名称
	private int heroBaseId;// 基表英雄id
	private int heroExtId;// 英雄扩展id
	private boolean isOnline;// 是否在线
	private int lv; // 玩家等级
	private int lvExp; // 玩家等级经验
	private int vipExp; // 玩家vip经验
	private int vipLv; // 玩家vip等级
	private int power;// 体力
	private int gold; // 玩家金币数量
	private int diamond; // 玩家钻石数量
	private String nickname; // 玩家角色名称
	private Date gagEndTime; // 玩家禁言结束时间
	private Date bsTime; // 封号开始时间
	private Date beTime; // 封号结束时间
	private Date createTime;// 创建时间
	private Date loginTime;// 上线时间
	private Date loginOutTime;// 下线时间
	private int fight; // 玩家当前战斗力
	private int onLineTime;// 在线时长
	private String clientModel;// 手机型号
	private String systemName;// 系统名称
	private String systemVersion;// 系统版本
	private Map<Integer, Property> property;// 属性
	private int serverId;// 服务器id
	private int headId;// 头像id
	private String moduleUseInfo;// 模块今天已经使用次数
	private String moduleBuyInfo;// 模块今天已经购买次数
	private int guildId;// 公会id
	private String guildName;// 公会名称
	private int starNum;// 副本获得的星星数量
	private int roomType;// 房间类型1 神兽副本
	private int roomId;// 房间号
	private PlayerPostion postion;// 玩家位置

	public int getAccountId() {
		return accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public int getHeroBaseId() {
		return heroBaseId;
	}

	public int getHeroExtId() {
		return heroExtId;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public int getLv() {
		return lv;
	}

	public int getLvExp() {
		return lvExp;
	}

	public int getVipExp() {
		return vipExp;
	}

	public int getVipLv() {
		return vipLv;
	}

	public int getPower() {
		return power;
	}

	public int getGold() {
		return gold;
	}

	public int getDiamond() {
		return diamond;
	}

	public String getNickname() {
		return nickname;
	}

	public Date getGagEndTime() {
		return gagEndTime;
	}

	public Date getBsTime() {
		return bsTime;
	}

	public Date getBeTime() {
		return beTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public Date getLoginOutTime() {
		return loginOutTime;
	}

	public int getFight() {
		return fight;
	}

	public int getOnLineTime() {
		return onLineTime;
	}

	public String getClientModel() {
		return clientModel;
	}

	public String getSystemName() {
		return systemName;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public Map<Integer, Property> getProperty() {
		return property;
	}

	public int getServerId() {
		return serverId;
	}

	public int getHeadId() {
		return headId;
	}

	public String getModuleUseInfo() {
		return moduleUseInfo;
	}

	public String getModuleBuyInfo() {
		return moduleBuyInfo;
	}

	public int getGuildId() {
		return guildId;
	}

	public String getGuildName() {
		return guildName;
	}

	public int getStarNum() {
		return starNum;
	}

	public int getRoomType() {
		return roomType;
	}

	public int getRoomId() {
		return roomId;
	}

	public PlayerPostion getPostion() {
		return postion;
	}

	public void setAccountId(int accountId) {
		this.op = Option.Update;
		this.accountId = accountId;
	}

	public void setAccountName(String accountName) {
		this.op = Option.Update;
		this.accountName = accountName;
	}

	public void setHeroBaseId(int heroBaseId) {
		this.op = Option.Update;
		this.heroBaseId = heroBaseId;
	}

	public void setHeroExtId(int heroExtId) {
		this.op = Option.Update;
		this.heroExtId = heroExtId;
	}

	public void setOnline(boolean isOnline) {
		this.op = Option.Update;
		this.isOnline = isOnline;
	}

	public void setLv(int lv) {
		this.op = Option.Update;
		this.lv = lv;
	}

	public void setLvExp(int lvExp) {
		this.op = Option.Update;
		this.lvExp = lvExp;
	}

	public void setVipExp(int vipExp) {
		this.op = Option.Update;
		this.vipExp = vipExp;
	}

	public void setVipLv(int vipLv) {
		this.op = Option.Update;
		this.vipLv = vipLv;
	}

	public void setPower(int power) {
		this.op = Option.Update;
		this.power = power;
	}

	public void setGold(int gold) {
		this.op = Option.Update;
		this.gold = gold;
	}

	public void setDiamond(int diamond) {
		this.op = Option.Update;
		this.diamond = diamond;
	}

	public void setNickname(String nickname) {
		this.op = Option.Update;
		this.nickname = nickname;
	}

	public void setGagEndTime(Date gagEndTime) {
		this.op = Option.Update;
		this.gagEndTime = gagEndTime;
	}

	public void setBsTime(Date bsTime) {
		this.op = Option.Update;
		this.bsTime = bsTime;
	}

	public void setBeTime(Date beTime) {
		this.op = Option.Update;
		this.beTime = beTime;
	}

	public void setCreateTime(Date createTime) {
		this.op = Option.Update;
		this.createTime = createTime;
	}

	public void setLoginTime(Date loginTime) {
		this.op = Option.Update;
		this.loginTime = loginTime;
	}

	public void setLoginOutTime(Date loginOutTime) {
		this.op = Option.Update;
		this.loginOutTime = loginOutTime;
	}

	public void setFight(int fight) {
		this.op = Option.Update;
		this.fight = fight;
	}

	public void setOnLineTime(int onLineTime) {
		this.op = Option.Update;
		this.onLineTime = onLineTime;
	}

	public void setClientModel(String clientModel) {
		this.op = Option.Update;
		this.clientModel = clientModel;
	}

	public void setSystemName(String systemName) {
		this.op = Option.Update;
		this.systemName = systemName;
	}

	public void setSystemVersion(String systemVersion) {
		this.op = Option.Update;
		this.systemVersion = systemVersion;
	}

	public void setProperty(Map<Integer, Property> property) {
		this.op = Option.Update;
		this.property = property;
	}

	public void setServerId(int serverId) {
		this.op = Option.Update;
		this.serverId = serverId;
	}

	public void setHeadId(int headId) {
		this.op = Option.Update;
		this.headId = headId;
	}

	public void setModuleUseInfo(String moduleUseInfo) {
		this.op = Option.Update;
		this.moduleUseInfo = moduleUseInfo;
	}

	public void setModuleBuyInfo(String moduleBuyInfo) {
		this.op = Option.Update;
		this.moduleBuyInfo = moduleBuyInfo;
	}

	public void setGuildId(int guildId) {
		this.op = Option.Update;
		this.guildId = guildId;
	}

	public void setGuildName(String guildName) {
		this.op = Option.Update;
		this.guildName = guildName;
	}

	public void setStarNum(int starNum) {
		this.op = Option.Update;
		this.starNum = starNum;
	}

	public void setRoomType(int roomType) {
		this.op = Option.Update;
		this.roomType = roomType;
	}

	public void setRoomId(int roomId) {
		this.op = Option.Update;
		this.roomId = roomId;
	}

	public void setPostion(PlayerPostion postion) {
		this.op = Option.Update;
		this.postion = postion;
	}

}