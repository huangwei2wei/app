package com.app.empire.world.entity.mongo;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import com.app.db.mongo.entity.IEntity;

/**
 * 角色表
 * 
 * @author doter
 */

@Document(collection = "player")
public class Player extends IEntity {
	private int accountId; // 玩家帐号ID
	private String accountName; // 玩家帐号名称
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
	private String property;// 属性
	private int serverId;// 服务器id
	private int headId;// 头像id
	private String moduleUseInfo;// 模块今天已经使用次数
	private String moduleBuyInfo;// 模块今天已经购买次数
	private int guildId;// 公会id
	private String guildName;// 公会名称
	private int starNum;// 副本获得的星星数量
	private int roomType;// 房间类型1 神兽副本
	private int roomId;// 房间号
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
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
	public int getVipExp() {
		return vipExp;
	}
	public void setVipExp(int vipExp) {
		this.vipExp = vipExp;
	}
	public int getVipLv() {
		return vipLv;
	}
	public void setVipLv(int vipLv) {
		this.vipLv = vipLv;
	}
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getDiamond() {
		return diamond;
	}
	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Date getGagEndTime() {
		return gagEndTime;
	}
	public void setGagEndTime(Date gagEndTime) {
		this.gagEndTime = gagEndTime;
	}
	public Date getBsTime() {
		return bsTime;
	}
	public void setBsTime(Date bsTime) {
		this.bsTime = bsTime;
	}
	public Date getBeTime() {
		return beTime;
	}
	public void setBeTime(Date beTime) {
		this.beTime = beTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public Date getLoginOutTime() {
		return loginOutTime;
	}
	public void setLoginOutTime(Date loginOutTime) {
		this.loginOutTime = loginOutTime;
	}
	public int getFight() {
		return fight;
	}
	public void setFight(int fight) {
		this.fight = fight;
	}
	public int getOnLineTime() {
		return onLineTime;
	}
	public void setOnLineTime(int onLineTime) {
		this.onLineTime = onLineTime;
	}
	public String getClientModel() {
		return clientModel;
	}
	public void setClientModel(String clientModel) {
		this.clientModel = clientModel;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public String getSystemVersion() {
		return systemVersion;
	}
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public int getHeadId() {
		return headId;
	}
	public void setHeadId(int headId) {
		this.headId = headId;
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
	public int getGuildId() {
		return guildId;
	}
	public void setGuildId(int guildId) {
		this.guildId = guildId;
	}
	public String getGuildName() {
		return guildName;
	}
	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}
	public int getStarNum() {
		return starNum;
	}
	public void setStarNum(int starNum) {
		this.starNum = starNum;
	}
	public int getRoomType() {
		return roomType;
	}
	public void setRoomType(int roomType) {
		this.roomType = roomType;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

}