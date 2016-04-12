package com.app.empire.world.server.handler.purchase;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.purchase.SendProductId;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 商品列表
 * 
 * @author Administrator
 */
public class SendProductIdHandler implements IDataHandler {
	private Logger log = Logger.getLogger("rechargeLog");

	// 商品列表
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		SendProductId sendProductId = (SendProductId) data;
		try {
			log.info("player:" + worldPlayer.getPlayer().getId() + "-----------productid:" + sendProductId.getProductId());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
