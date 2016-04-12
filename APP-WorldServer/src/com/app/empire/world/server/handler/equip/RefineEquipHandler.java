package com.app.empire.world.server.handler.equip;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.app.empire.protocol.data.equip.RefineEquip;
import com.app.empire.protocol.data.equip.RefineEquipOk;
import com.app.empire.world.entity.mongo.HeroEquipGoods;
import com.app.empire.world.entity.mongo.PlayerHeroEquip;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 装备精炼
 * 
 * @author doter
 */
public class RefineEquipHandler implements IDataHandler {
	private Logger log = Logger.getLogger(RefineEquipHandler.class);
	public AbstractData handle(AbstractData data) throws Exception {
		RefineEquip refineEquip = (RefineEquip) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int heroId = refineEquip.getHeroId(); // 英雄流水id
		int rank = refineEquip.getRank(); // 军阶
		int equipNo = refineEquip.getEquipNo(); // 精炼的装备栏编号
		int[] goodsId = refineEquip.getGoodsId(); // 玩家物品流水id
		int[] goodsNum = refineEquip.getGoodsNum(); // 材料对应数量
		try {
			PlayerHeroEquip playerHeroEquip = ServiceManager.getManager().getPlayerEquipService().refineEquip(worldPlayer, heroId, rank, equipNo, ArrayUtils.toObject(goodsId), goodsNum);
			RefineEquipOk refineEquipOk = new RefineEquipOk(data.getSessionId(), data.getSerial());
			refineEquipOk.setHeroId(playerHeroEquip.getId());// 英雄流水id
			refineEquipOk.setRank(playerHeroEquip.getRank());
			refineEquipOk.setEquipNo(equipNo);
			HeroEquipGoods heroEquipGoods = playerHeroEquip.getEquip().get(equipNo);
			refineEquipOk.setProAdd(heroEquipGoods.getProAdd());
			refineEquipOk.setGoodsStar(heroEquipGoods.getGoodsStar());
			refineEquipOk.setGoodsExp(heroEquipGoods.getGoodsExp());

			return refineEquipOk;
		} catch (PlayerDataException ex) {
			this.log.error(ex);
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}

}
