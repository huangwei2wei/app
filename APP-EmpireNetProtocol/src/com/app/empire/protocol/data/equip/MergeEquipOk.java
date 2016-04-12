package com.app.empire.protocol.data.equip;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 合成装备
 */
public class MergeEquipOk extends AbstractData {
	int status; // 0失败 1成功

	public MergeEquipOk(int sessionId, int serial) {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_MergeEquipOk, sessionId, serial);
	}

	public MergeEquipOk() {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_MergeEquipOk);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
