package com.app.empire.world.server.handler.equip;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.equip.MergeEquip;
import com.app.empire.protocol.data.equip.MergeEquipOk;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 合成装备并穿戴
 * 
 * @since JDK 1.7
 */
public class MergeEquipHandler implements IDataHandler {
	private Logger log  = Logger.getLogger(MergeEquipHandler.class);
	public AbstractData handle(AbstractData data) throws Exception {
		MergeEquip syntEquip = (MergeEquip) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int equipNo = syntEquip.getEquipNo();
		int goodsId = syntEquip.getGoodsId();
		int rank = syntEquip.getRank();
		int heroId = syntEquip.getHeroId();
		try {
			ServiceManager.getManager().getPlayerEquipService().mergeAndWearEquip(worldPlayer, heroId, rank, equipNo, goodsId);
			MergeEquipOk equipOk = new MergeEquipOk(syntEquip.getSessionId(), syntEquip.getSerial());
			equipOk.setStatus(1);
			return equipOk;
		} catch (PlayerDataException ex) {
			this.log.error(ex);
			throw new ProtocolException(ex.getMessage(), syntEquip.getSerial(), syntEquip.getSessionId(), syntEquip.getType(), syntEquip.getSubType());
		}
	}
}