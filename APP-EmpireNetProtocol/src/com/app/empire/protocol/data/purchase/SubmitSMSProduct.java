package com.app.empire.protocol.data.purchase;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class SubmitSMSProduct extends AbstractData {
    private String serialNum;
	public SubmitSMSProduct(int sessionId, int serial) {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_SubmitSMSProduct, sessionId, serial);
	}

	public SubmitSMSProduct() {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_SubmitSMSProduct);
	}

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
}
