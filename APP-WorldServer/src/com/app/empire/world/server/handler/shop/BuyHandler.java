package com.app.empire.world.server.handler.shop;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.shop.Buy;
import com.app.empire.protocol.data.shop.BuyOk;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

public class BuyHandler implements IDataHandler {
	private Logger log = Logger.getLogger(GetShopHandler.class.getPackage().getName());

	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		Buy buy = (Buy) data;
		int shopType = buy.getShopType();// 商店类型 1、神秘商店
		int id = buy.getId();// 配置表id
		int num = buy.getNum();// 数量
		try {
			ServiceManager.getManager().getPlayerShopService().buyShop(worldPlayer, shopType, id, num);
			BuyOk ok = new BuyOk(data.getSessionId(), data.getSerial());
			session.write(ok);
		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
