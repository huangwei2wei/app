package com.app.empire.world.model;

import org.apache.commons.configuration.Configuration;

public class ServerConfig {
	private boolean isMaintance = true;
	private int maxLevel;
	private String area;
	private String areaId;
	private int machineCode = 0;
	private String group;
	private boolean cross;
	private String version;// 服务器版本用于跨服配对
	private String serverName;// 服务器名称用于跨服配对

	public ServerConfig(Configuration configuration) {
		maxLevel = configuration.getInt("maxlevel", 99);
		setArea(configuration.getString("area"));
		setMachineCode(configuration.getInt("machinecode"));
		setAreaId(getArea() + "_" + getMachineCode());
		setGroup(configuration.getString("group"));
		setVersion(configuration.getString("version"));
		String battleip = configuration.getString("battleip");
		String battleport = configuration.getString("battleport");
		setServerName(configuration.getString("servername"));

		if (null != battleip && null != battleport)
			cross = true;
	}

	/**
	 * 游戏是否在维护
	 * 
	 * @return
	 */
	public boolean isMaintance() {
		return isMaintance;
	}

	public void setMaintance(boolean isMaintance) {
		this.isMaintance = isMaintance;
	}

	/**
	 * 游戏最大级别
	 * 
	 * @return
	 */
	public int getMaxLevel() {
		return maxLevel;
	}

	/**
	 * 服务器ID
	 * 
	 * @return
	 */
	public int getMachineCode() {
		return machineCode;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public void setMachineCode(int machineCode) {
		this.machineCode = machineCode;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * 是否开启跨服战斗
	 * 
	 * @return
	 */
	public boolean isCross() {
		return cross;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getServerName() {
		return serverName == null ? "" : serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}
