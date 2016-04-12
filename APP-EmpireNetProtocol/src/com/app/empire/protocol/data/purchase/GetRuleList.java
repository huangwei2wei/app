package com.app.empire.protocol.data.purchase;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetRuleList extends AbstractData {
	public GetRuleList(int sessionId, int serial) {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_GetRuleList, sessionId, serial);
	}

	public GetRuleList() {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_GetRuleList);
	}
}
