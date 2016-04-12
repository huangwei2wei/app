package com.app.empire.world.server.handler.npc;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.npc.Receive;
import com.app.empire.protocol.data.npc.ReceiveOK;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;
/**
 * 领取
 * 
 * @author doter
 * 
 */
public class ReceiveHandler implements IDataHandler {
	private Logger log = Logger.getLogger(BuyHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		Receive buy = (Receive) data;
		int npcType = buy.getNpcType();
		try {
			int value = ServiceManager.getManager().getPlayerNpcService().receiveNpc(worldPlayer, npcType);
			ReceiveOK ok = new ReceiveOK(data.getSessionId(), data.getSerial());
			ok.setNpcType(npcType);
			ok.setValue(value);
			return ok;
		} catch (Exception e) {
			throw new ProtocolException(e.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
