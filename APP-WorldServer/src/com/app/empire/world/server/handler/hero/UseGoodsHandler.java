package com.app.empire.world.server.handler.hero;

import com.app.empire.protocol.data.hero.UseGoods;
import com.app.empire.protocol.data.hero.UseGoodsOK;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

public class UseGoodsHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		UseGoods useGoods = (UseGoods) data;
		int heroId = useGoods.getHeroId();// 英雄流水id
		int goodsId = useGoods.getGoodsId();// 物品流水id（经验丹
		int num = useGoods.getNum();
		try {
			ServiceManager.getManager().getPlayerHeroService().useGoods(worldPlayer, heroId, goodsId, num);
			UseGoodsOK ok = new UseGoodsOK(data.getSessionId(), data.getSerial());
			return ok;
		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
