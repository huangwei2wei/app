package com.app.empire.protocol.data.equip;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class ActivateAchieveOk extends AbstractData {
	private int achieveProAdd; // 装备成就属性加成
	private int[] achieve; // 1:精炼成就，
	private int[] achieve2;// 2：收集成就

	public ActivateAchieveOk(int sessionId, int serial) {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_ActivateAchieveOk, sessionId, serial);
	}
	public ActivateAchieveOk() {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_ActivateAchieveOk);
	}
	public int getAchieveProAdd() {
		return achieveProAdd;
	}
	public void setAchieveProAdd(int achieveProAdd) {
		this.achieveProAdd = achieveProAdd;
	}
	public int[] getAchieve() {
		return achieve;
	}
	public void setAchieve(int[] achieve) {
		this.achieve = achieve;
	}
	public int[] getAchieve2() {
		return achieve2;
	}
	public void setAchieve2(int[] achieve2) {
		this.achieve2 = achieve2;
	}

}
