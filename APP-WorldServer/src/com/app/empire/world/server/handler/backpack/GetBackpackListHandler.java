package com.app.empire.world.server.handler.backpack;

import java.util.List;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.backpack.GetBackpackList;
import com.app.empire.protocol.data.backpack.GetBackpackListOk;
import com.app.empire.world.entity.mongo.PlayerGoods;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取背包物品
 */
public class GetBackpackListHandler implements IDataHandler {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger("backpack");

	public void handle(AbstractData data) throws Exception {
		GetBackpackList equipList = (GetBackpackList) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer player = session.getPlayer(data.getSessionId());

		GetBackpackListOk listOk = new GetBackpackListOk(equipList.getSessionId(), equipList.getSerial());
		List<PlayerGoods> goodsInfoList = ServiceManager.getManager().getPlayerGoodsService().getBackpackList(player);
		int i = 0;
		int[] id = new int[goodsInfoList.size()];
		int[] goodsId = new int[goodsInfoList.size()];
		int[] goodsNum = new int[goodsInfoList.size()];
		long[] time = new long[goodsInfoList.size()];
		for (PlayerGoods goodsInfo : goodsInfoList) {
			id[i] = goodsInfo.getId();
			goodsId[i] = goodsInfo.getGoodsId();
			goodsNum[i] = goodsInfo.getGoodsNum();
			time[i] = goodsInfo.getDateTime().getTime();
			i += 1;
		}
		listOk.setId(id);
		listOk.setGoodsId(goodsId);
		listOk.setGoodsNum(goodsNum);
		listOk.setTime(time);
		session.write(  listOk);
	}
}