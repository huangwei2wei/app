package com.app.empire.world.server.handler.copymap;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.copymap.AcessCopyMap;
import com.app.empire.protocol.data.copymap.AcessCopyMapOk;
import com.app.empire.world.entity.mongo.PlayerCopyMap;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 接取副本
 * 
 * @author doter
 * 
 */
public class AcessCopyMapHandler implements IDataHandler {
	private Logger log = Logger.getLogger(AcessCopyMapHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		AcessCopyMap acessCopyMap = (AcessCopyMap) data;
		byte copyMapType = acessCopyMap.getCopyMapType();// 副本类型1：主线副本
		int copyMapId = acessCopyMap.getCopyMapId();// 副本id
		try {
			if (copyMapType == 1) {// 主线副本
				PlayerCopyMap playerCopyMap = ServiceManager.getManager().getPlayerCopyMapService().acessCopyMap(worldPlayer, copyMapId);
				AcessCopyMapOk ok = new AcessCopyMapOk(data.getSessionId(), data.getSerial());
				ok.setId(playerCopyMap.getId());
				return ok;
			}
		} catch (PlayerDataException e) {
			this.log.info(e);
			// e.printStackTrace();
			throw new ProtocolException(e.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
		return null;
	}
}
