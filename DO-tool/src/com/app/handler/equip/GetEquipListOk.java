package com.app.handler.equip;

import com.app.net.IConnector;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class GetEquipListOk implements IDataHandler {
	public void handle(AbstractData data) throws Exception {
		IConnector connector = data.getSource();
		
	}
}