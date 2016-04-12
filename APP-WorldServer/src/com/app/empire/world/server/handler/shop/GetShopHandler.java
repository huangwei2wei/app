package com.app.empire.world.server.handler.shop;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.shop.GetShop;
import com.app.empire.protocol.data.shop.GetShopOk;
import com.app.empire.world.common.util.DateUtil;
import com.app.empire.world.entity.mongo.PlayerShop;
import com.app.empire.world.entity.mongo.Product;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取神秘商店物品
 * 
 * @author doter
 * 
 */

public class GetShopHandler implements IDataHandler {
	private Logger log = Logger.getLogger(GetShopHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		GetShop getShop = (GetShop) data;
		int shopType = getShop.getShopType();// 商店类型 1、神秘商店
		PlayerShop playerShop = ServiceManager.getManager().getPlayerShopService().getPlayerShop(worldPlayer, shopType);
		int discount = playerShop.getDiscount();
		Date createTime = playerShop.getCreateTime();
		Map<Integer, Product> products = playerShop.getProducts();
		Map<String, Integer> refreshNumInfo = playerShop.getRefreshNum();
		int size = products.size();
		int[] id = new int[size];// 配置表id
		int[] goodsId = new int[size];// 物品id
		int[] num = new int[size];// 物品数量
		int[] gold = new int[size];// 消耗金币
		int[] diamond = new int[size];// 消耗钻石
		int refreshNum = 0;// 刷新次数
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
		if (refreshNumInfo != null) {
			String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
			if (refreshNumInfo.containsKey(dateStr))
				refreshNum = refreshNumInfo.get(dateStr);
		}

		GetShopOk ok = new GetShopOk(data.getSessionId(), data.getSerial());
		ok.setDiscount(discount);
		ok.setCreateTime(createTime.getTime());
		ok.setId(id);
		ok.setGoodsId(goodsId);
		ok.setNum(num);
		ok.setGold(gold);
		ok.setDiamond(diamond);
		ok.setRefreshNum(refreshNum);
		return ok;
	}
}
