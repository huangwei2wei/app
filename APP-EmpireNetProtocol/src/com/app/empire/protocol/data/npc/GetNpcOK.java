package com.app.empire.protocol.data.npc;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetNpcOK extends AbstractData {
	private int[] npcType;// npc 类型 1 金币npc ，2钻石npc
	private int[] lv;// 升级成功后的等级
	private long[] upgradeTime;// 升级时间
	private long[] receiveTime;// 领取时间
	private int[] buyCount;// 当天购买次数，日期->次数

	public GetNpcOK(int sessionId, int serial) {
		super(Protocol.MAIN_NPC, Protocol.NPC_GetNpcOK, sessionId, serial);
	}

	public GetNpcOK() {
		super(Protocol.MAIN_NPC, Protocol.NPC_GetNpcOK);
	}

	public int[] getNpcType() {
		return npcType;
	}

	public void setNpcType(int[] npcType) {
		this.npcType = npcType;
	}

	public int[] getLv() {
		return lv;
	}

	public void setLv(int[] lv) {
		this.lv = lv;
	}

	public long[] getUpgradeTime() {
		return upgradeTime;
	}

	public void setUpgradeTime(long[] upgradeTime) {
		this.upgradeTime = upgradeTime;
	}

	public long[] getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(long[] receiveTime) {
		this.receiveTime = receiveTime;
	}

	public int[] getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(int[] buyCount) {
		this.buyCount = buyCount;
	}

}
