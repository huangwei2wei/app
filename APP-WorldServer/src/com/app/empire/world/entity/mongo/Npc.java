package com.app.empire.world.entity.mongo;

import java.util.Date;
import java.util.Map;
/**
 * NPC entity
 * 
 * @author doter
 * 
 */
public class Npc {
	private int npcType;// npc 类型 1 金币npc ，2钻石npc
	private int lv;// 等级
	private Date upgradeTime;// 升级时间
	private Date receiveTime;// 领取时间
	private Map<String, Integer> buyCount;// 当天购买次数，日期->次数

	public int getNpcType() {
		return npcType;
	}
	public void setNpcType(int npcType) {
		this.npcType = npcType;
	}
	public int getLv() {
		return lv;
	}
	public void setLv(int lv) {
		this.lv = lv;
	}
	public Date getUpgradeTime() {
		return upgradeTime;
	}
	public void setUpgradeTime(Date upgradeTime) {
		this.upgradeTime = upgradeTime;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
	public Map<String, Integer> getBuyCount() {
		return buyCount;
	}
	public void setBuyCount(Map<String, Integer> buyCount) {
		this.buyCount = buyCount;
	}

}
