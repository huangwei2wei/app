package com.app.empire.world.server.handler.backpack;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.backpack.SellGoods;
import com.app.empire.protocol.data.backpack.SellGoodsOk;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 出售背包物品
 */
public class SellGoodsHandler implements IDataHandler {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger("backpack");
	public void handle(AbstractData data) throws Exception {
		SellGoods sellGoods = (SellGoods) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer player = session.getPlayer(data.getSessionId());

		try {
			int price = ServiceManager.getManager().getPlayerGoodsService().sellGoods(player, sellGoods.getId(), sellGoods.getGoodsNum());
			SellGoodsOk sellGoodsOk = new SellGoodsOk(sellGoods.getSessionId(), sellGoods.getSerial());
			sellGoodsOk.setName("gold");
			sellGoodsOk.setPrice(price);
			session.write(  sellGoodsOk);
		} catch (Exception e) {
			throw new ProtocolException(e.getMessage(), sellGoods.getSerial(), sellGoods.getSessionId(), sellGoods.getType(), sellGoods.getSubType());
		}

	}
}