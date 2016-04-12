package com.app.empire.world.server.handler.copymap;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.copymap.CompleteCopyMap;
import com.app.empire.protocol.data.copymap.CompleteCopyMapOk;
import com.app.empire.world.entity.mongo.PlayerCopyMap;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 完成副本
 * 
 * @author doter
 * 
 */
public class CompleteCopyMapHandler implements IDataHandler {
	private Logger log = Logger.getLogger(AcessCopyMapHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		CompleteCopyMap completeCopyMap = (CompleteCopyMap) data;
		byte copyMapType = completeCopyMap.getCopyMapType();// 副本类型1：主线副本
		int copyMapId = completeCopyMap.getCopyMapId();// // 副本id
		int star = completeCopyMap.getStar();
		try {
			if (copyMapType == 1) {// 主线副本
				PlayerCopyMap playerCopyMap = ServiceManager.getManager().getPlayerCopyMapService().completeCopyMap(worldPlayer, copyMapId, star);
				String randomAward = playerCopyMap.getRandomAward();
				CompleteCopyMapOk ok = new CompleteCopyMapOk(data.getSessionId(), data.getSerial());
				ok.setId(playerCopyMap.getId());
				ok.setAward(randomAward);
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
