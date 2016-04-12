package com.app.empire.gameaccount.bean;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import com.app.db.mongo.entity.IEntity;

/**
 * The persistent class for the tab_account database table.
 * 
 * @author doter
 */

@Document(collection = "account")
public class Account extends IEntity {
	/**
	 * 账号表
	 */
	private String username;
	private String password;
	private Integer status;// 状态
	private Date createTime;
	private Date lastLoginTime;// 上次登录时间
	private String version;// 游戏版本
	private Integer totalLoginTimes;// 总登录次数
	private Integer onLineTime;// 在线时长
	private Integer maxLevel;// 该账号所有角色最高等级
	private List<String> serverIds;// 所登录过的游戏区名
	private Integer machinecode;// 服务器码
	private Integer channel;// 渠道号
	private String systemName;// 客户端系统
	private String clientModel;// 客户端型号
	private String systemVersion;// 客户端系统版本
	private String ipAddress;// ip 地址

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Integer getTotalLoginTimes() {
		return totalLoginTimes;
	}
	public void setTotalLoginTimes(Integer totalLoginTimes) {
		this.totalLoginTimes = totalLoginTimes;
	}
	public Integer getOnLineTime() {
		return onLineTime;
	}
	public void setOnLineTime(Integer onLineTime) {
		this.onLineTime = onLineTime;
	}
	public Integer getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(Integer maxLevel) {
		this.maxLevel = maxLevel;
	}
	public List<String> getServerIds() {
		return serverIds;
	}
	public void setServerIds(List<String> serverIds) {
		this.serverIds = serverIds;
	}
	public Integer getMachinecode() {
		return machinecode;
	}
	public void setMachinecode(Integer machinecode) {
		this.machinecode = machinecode;
	}
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		this.channel = channel;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public String getClientModel() {
		return clientModel;
	}
	public void setClientModel(String clientModel) {
		this.clientModel = clientModel;
	}
	public String getSystemVersion() {
		return systemVersion;
	}
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}