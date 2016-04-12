package com.app.empire.world.server.handler.purchase;

import org.apache.log4j.Logger;

import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 商品列表
 * 
 * @author Administrator
 */
public class GetProductIdListHandler implements IDataHandler {
	Logger log = Logger.getLogger(GetProductIdListHandler.class);

	// 商品列表
	public AbstractData handle(AbstractData data) throws Exception {
//		ConnectSession session = (ConnectSession) data.getHandlerSource();
//		GetProductIdList getProductIdList = (GetProductIdList) data;
//		WorldPlayer player = session.getPlayer(data.getSessionId());
//		try {
//			List<Recharge> rechargeList = ServiceManager.getManager().getRechargeService()
//					.getRechargeListByChannel(getProductIdList.getChannelId());
//			String[] ids = new String[rechargeList.size()];
//			String[] icons = new String[rechargeList.size()];
//			int[] numbers = new int[rechargeList.size()];
//			int[] rate = new int[rechargeList.size()];
//			Recharge recharge;
//			for (int i = 0; i < rechargeList.size(); i++) {
//				recharge = rechargeList.get(i);
//				ids[i] = recharge.getCommodityId();
//				icons[i] = recharge.getIcon();
//				// 以前版本是给客户端价格，现改成给钻石数。价格由客户端自己获得
//				numbers[i] = recharge.getNumber();
//				rate[i] = recharge.getRate();
//			}
//			SendProductIdList sendProductIdList = new SendProductIdList(data.getSessionId(), data.getSerial());
//			sendProductIdList.setIds(ids);
//			sendProductIdList.setIcons(icons);
//			sendProductIdList.setNumbers(numbers);
//			sendProductIdList.setPriceunit(ServiceManager.getManager().getConfiguration().getString("priceunit", "$"));
//			sendProductIdList.setRate(rate);
//			sendProductIdList.setAmount(player.getPlayer().getAmount());
//			session.write(sendProductIdList);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		return null;
	}
}
