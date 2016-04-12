package com.app.empire.protocol.data.npc;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * npc 升级ok
 * 
 * @author doter
 * 
 */

public class UpgradeOK extends AbstractData {
	private int lv;// 升级后的等级
	private long upgradeTime;// 升级时间

	public UpgradeOK(int sessionId, int serial) {
		super(Protocol.MAIN_NPC, Protocol.NPC_UpgradeOK, sessionId, serial);
	}

	public UpgradeOK() {
		super(Protocol.MAIN_NPC, Protocol.NPC_UpgradeOK);
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public long getUpgradeTime() {
		return upgradeTime;
	}

	public void setUpgradeTime(long upgradeTime) {
		this.upgradeTime = upgradeTime;
	}

}
