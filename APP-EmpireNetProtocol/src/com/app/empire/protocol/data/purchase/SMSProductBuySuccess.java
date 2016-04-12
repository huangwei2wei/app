package com.app.empire.protocol.data.purchase;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class SMSProductBuySuccess extends AbstractData {
    private String serialNum;
    private int    id;
    private int    hasGrant;
	public SMSProductBuySuccess(int sessionId, int serial) {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_SMSProductBuySuccess, sessionId, serial);
	}

	public SMSProductBuySuccess() {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_SMSProductBuySuccess);
	}

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHasGrant() {
        return hasGrant;
    }

    public void setHasGrant(int hasGrant) {
        this.hasGrant = hasGrant;
    }
}
