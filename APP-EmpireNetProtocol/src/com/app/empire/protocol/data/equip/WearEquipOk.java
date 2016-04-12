package com.app.empire.protocol.data.equip;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class WearEquipOk extends AbstractData {
	int status; 
	
    public WearEquipOk(int sessionId, int serial) {
        super(Protocol.MAIN_EQUIP, Protocol.EQUIP_WearEquipOk, sessionId, serial);
    }

    public WearEquipOk() {
        super(Protocol.MAIN_EQUIP, Protocol.EQUIP_WearEquipOk);
    }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
    
}
