package com.app.empire.world.server.handler.equip;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.equip.WearEquip;
import com.app.empire.protocol.data.equip.WearEquipOk;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 穿戴装备
 * 
 * @since JDK 1.7
 */
public class WearEquipHandler implements IDataHandler {
	private Logger log = Logger.getLogger(MergeEquipHandler.class);
	public AbstractData handle(AbstractData data) throws Exception {
		WearEquip wearEquip = (WearEquip) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer player = session.getPlayer(data.getSessionId());

		int goodsId = wearEquip.getGoodsId();
		int heroId = wearEquip.getHeroId();
		int rank = wearEquip.getRank();
		int equipNo = wearEquip.getEquipNo();
		try {
			ServiceManager.getManager().getPlayerEquipService().wearEquip(player, goodsId, heroId, rank, equipNo);
			WearEquipOk equipOk = new WearEquipOk(wearEquip.getSessionId(), wearEquip.getSerial());
			equipOk.setStatus(1);
			return equipOk;
		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}