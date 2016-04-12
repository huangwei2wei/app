package com.app.empire.world.server.handler.equip;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.app.empire.protocol.data.equip.ActivateAchieve;
import com.app.empire.protocol.data.equip.ActivateAchieveOk;
import com.app.empire.world.entity.mongo.PlayerHeroEquip;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;
/**
 * 激活装备成就
 * 
 * @author doter
 */
public class ActivateAchieveHandler implements IDataHandler {
	private Logger log = Logger.getLogger(RefineEquipHandler.class);
	public AbstractData handle(AbstractData data) throws Exception {
		ActivateAchieve activateAchieve = (ActivateAchieve) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int heroId = activateAchieve.getHeroId();// 英雄流水id
		int achieveId = activateAchieve.getAchieveId();// 要激活的成就id
		try {
			PlayerHeroEquip playerHeroEquip = ServiceManager.getManager().getPlayerEquipService().Activate(worldPlayer, heroId, achieveId);
			ActivateAchieveOk activateAchieveOk = new ActivateAchieveOk(data.getSessionId(), data.getSerial());

			List<Integer> achieve = playerHeroEquip.getAchieve();
			List<Integer> achieve2 = playerHeroEquip.getAchieve2();

			activateAchieveOk.setAchieveProAdd(playerHeroEquip.getAchieveProAdd());
			activateAchieveOk.setAchieve(achieve != null ? ArrayUtils.toPrimitive(achieve.toArray(new Integer[achieve.size()])) : new int[0]);
			activateAchieveOk.setAchieve2(achieve2 != null ? ArrayUtils.toPrimitive(achieve2.toArray(new Integer[achieve2.size()])) : new int[0]);
			return activateAchieveOk;
		} catch (PlayerDataException e) {
			throw new ProtocolException(e.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
