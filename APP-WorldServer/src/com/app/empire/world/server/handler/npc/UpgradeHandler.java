package com.app.empire.world.server.handler.npc;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.npc.Upgrade;
import com.app.empire.protocol.data.npc.UpgradeOK;
import com.app.empire.world.entity.mongo.Npc;
import com.app.empire.world.entity.mongo.PlayerNpc;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * npc 升级
 * 
 * @author doter
 * 
 */
public class UpgradeHandler implements IDataHandler {
	private Logger log = Logger.getLogger(UpgradeHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		Upgrade upgrade = (Upgrade) data;
		int npcType = upgrade.getNpcType();// npc 类型
		try {
			PlayerNpc playerNpc = ServiceManager.getManager().getPlayerNpcService().upgradePlayerNpc(worldPlayer, npcType);
			Npc npc = playerNpc.getNpc().get(npcType);
			UpgradeOK ok = new UpgradeOK(data.getSessionId(), data.getSerial());
			ok.setLv(npc.getLv());
			ok.setUpgradeTime(npc.getUpgradeTime().getTime());
			return ok;
		} catch (PlayerDataException e) {
			throw new ProtocolException(e.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
