package com.app.empire.world.server.handler.purchase;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.purchase.BuyFailed;
import com.app.empire.protocol.data.purchase.IOSSendProductCheckInfo;
import com.app.empire.world.common.util.Common;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 验证充值信息
 * 
 * @author Administrator
 */
public class IOSSendProductCheckInfoHandler implements IDataHandler {
	private Logger log = Logger.getLogger("orderInfoLog");
	private Logger errorLog = Logger.getLogger(IOSSendProductCheckInfoHandler.class);

	public AbstractData handle(AbstractData data) throws Exception {
		IOSSendProductCheckInfo iosSendProductCheckInfo = (IOSSendProductCheckInfo) data;
		WorldPlayer player = ServiceManager.getManager().getPlayerService().getPlayer(iosSendProductCheckInfo.getPlayerId());
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("playerId:");
			sb.append(iosSendProductCheckInfo.getPlayerId());
			sb.append("orderNum:");
			sb.append(iosSendProductCheckInfo.getOrderNum());
			sb.append("key:");
			sb.append(iosSendProductCheckInfo.getKey());
			sb.append("channelId:");
			sb.append(iosSendProductCheckInfo.getChannelId());
			log.info(sb.toString());
		} catch (Exception e) {
			errorLog.error(e, e);
		}
		if (iosSendProductCheckInfo.getKey() != null && iosSendProductCheckInfo.getKey().length() > 1000) {
//			ServiceManager.getManager().getPCRechargeService().addspci(player, data, RechargeService.IOSORDER);
		} else {
			int code = Common.CHAT_GUILD;
			if (iosSendProductCheckInfo.getKey().equals("-1")) {
				code = Common.CHAT_GUILD;
			}
			BuyFailed buyFailed = new BuyFailed();
			buyFailed.setOrderNum(iosSendProductCheckInfo.getOrderNum());
			buyFailed.setCode(code);
			player.sendData(buyFailed);
		}
		return null;
	}
	
}
