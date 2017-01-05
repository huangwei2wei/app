package com.app.empire.world.server.handler.shop;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.shop.Refresh;
import com.app.empire.protocol.data.shop.RefreshOk;
import com.app.empire.world.entity.mongo.PlayerShop;
import com.app.empire.world.entity.mongo.Product;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 刷新商店
 * 
 * @author doter
 * 
 */

public class RefreshHandler implements IDataHandler {
	private Logger log = Logger.getLogger(RefreshHandler.class.getPackage().getName());

	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		Refresh getShop = (Refresh) data;
		int shopType = getShop.getShopType();// 商店类型 1、神秘商店
		try {
			PlayerShop playerShop = ServiceManager.getManager().getPlayerShopService().refreshShop(worldPlayer);
			RefreshOk ok = new RefreshOk(data.getSessionId(), data.getSerial());

			int discount = playerShop.getDiscount();
			Date createTime = playerShop.getCreateTime();
			Map<Integer, Product> products = playerShop.getProducts();
			int size = products.size();
			int[] id = new int[size];// 配置表id
			int[] goodsId = new int[size];// 物品id
			int[] num = new int[size];// 物品数量
			int[] gold = new int[size];// 消耗金币
			int[] diamond = new int[size];// 消耗钻石
			int i = 0;
			for (Entry<Integer, Product> entry : products.entrySet()) {
				Product product = entry.getValue();
				id[i] = entry.getKey();
				goodsId[i] = product.getGoodsId();
				num[i] = product.getNum();
				gold[i] = product.getGold();
				diamond[i] = product.getDiamond();
				i++;
			}

			ok.setDiscount(discount);
			ok.setCreateTime(createTime.getTime());
			ok.setId(id);
			ok.setGoodsId(goodsId);
			ok.setNum(num);
			ok.setGold(gold);
			ok.setDiamond(diamond);
			session.write(ok);

		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}

	}
}
