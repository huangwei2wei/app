package com.app.empire.world.server.handler.backpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.app.empire.protocol.data.backpack.UseGoods;
import com.app.empire.protocol.data.backpack.UseGoodsOk;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 使用物品
 */
public class UseGoodsHandler implements IDataHandler {
	private Logger log = Logger.getLogger("backpack");
	public AbstractData handle(AbstractData data) throws Exception {
		UseGoods useGoods = (UseGoods) data;
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer player = session.getPlayer(data.getSessionId());
		try {
			ArrayList<HashMap<String, Integer>> useEffectList = ServiceManager.getManager().getPlayerGoodsService().useGoods(player, useGoods.getId(), useGoods.getGoodsNum());
			ArrayList<String> key = new ArrayList<String>();
			ArrayList<Integer> val = new ArrayList<Integer>();
			for (HashMap<String, Integer> hashMap : useEffectList) {
				for (Entry<String, Integer> entry : hashMap.entrySet()) {
					key.add(entry.getKey());
					val.add(entry.getValue());
				}
			}
			UseGoodsOk ok = new UseGoodsOk(useGoods.getSessionId(), useGoods.getSerial());
			ok.setKey(key.toArray(new String[key.size()]));
			ok.setValue(ArrayUtils.toPrimitive(val.toArray(new Integer[val.size()])));
			return ok;
		} catch (Exception e) {
			e.printStackTrace();
			this.log.info(e);
			throw new ProtocolException(e.getMessage(), useGoods.getSerial(), useGoods.getSessionId(), useGoods.getType(), useGoods.getSubType());
		}
	}
}