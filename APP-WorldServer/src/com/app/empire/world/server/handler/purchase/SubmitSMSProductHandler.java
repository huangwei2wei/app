package com.app.empire.world.server.handler.purchase;

import org.apache.log4j.Logger;

import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class SubmitSMSProductHandler implements IDataHandler {
	Logger log = Logger.getLogger(SubmitSMSProductHandler.class);

	public AbstractData handle(AbstractData data) throws Exception {
//		ConnectSession session = (ConnectSession) data.getHandlerSource();
//		SubmitSMSProduct submitSMSProduct = (SubmitSMSProduct) data;
//		WorldPlayer player = session.getPlayer(data.getSessionId());
//		try {
//			Order order = ServiceManager.getManager().getOrderService().getOrderBySerial(submitSMSProduct.getSerialNum());
//			if (order != null) {
//				SMSProductBuySuccess spbs = new SMSProductBuySuccess(data.getSessionId(), data.getSerial());
//				spbs.setSerialNum(order.getSerialNum());
//				spbs.setId(order.getPointId());
//				if (order.getStatus() == OrderDao.ORDER_STATUS_INIT) {
//					order.setStatus(OrderDao.ORDER_STATUS_BACK);
//					BillingPoint billingPoint = ServiceManager.getManager().getOrderService().getBillingPointById(order.getPointId());
//					// 如果玩家上一笔订单已回调并且（订单不为充值订单或者充值金额确定）则给玩家发放物品
//					if (!ServiceManager.getManager().getOrderService().isBeforeOrderHasNotCallBack(player.getId())
//							&& (billingPoint.getItemId() != Common.DIAMONDID || billingPoint.getCount() > 0)) {
//						if (billingPoint.getItemId() == Common.DIAMONDID) {
//							ServiceManager
//									.getManager()
//									.getPlayerService()
//									.addTicket(player, billingPoint.getCount(), 0, TradeService.ORIGIN_RECH, billingPoint.getPrice(),
//											order.getOrderNum(), "", order.getChannel() + "", "");
//						} else if (billingPoint.getItemId() == Common.GOLDID) {
//							try {// 记录订单日志
//								Client client = player.getClient();
//								int accountId = null == client ? 0 : client.getAccountId();
//								GameLogService.recharge(player.getId(), player.getLevel(), accountId, order.getChannel() + "", "",
//										order.getOrderNum(), billingPoint.getPrice(), 0, 0, billingPoint.getId().toString());
//							} catch (Exception e) {
//								log.error(e, e);
//							}
//							ServiceManager.getManager().getPlayerService()
//									.updatePlayerGold(player, billingPoint.getCount(), "计费点购买", billingPoint.getId() + "");
//						} else {
//							try {// 记录订单日志
//								Client client = player.getClient();
//								int accountId = null == client ? 0 : client.getAccountId();
//								GameLogService.recharge(player.getId(), player.getLevel(), accountId, order.getChannel() + "", "",
//										order.getOrderNum(), billingPoint.getPrice(), 0, 0, billingPoint.getId().toString());
//							} catch (Exception e) {
//								log.error(e, e);
//							}
//							int day = -1;
//							int count = -1;
//							if (billingPoint.getType() == 0) {
//								day = billingPoint.getCount();
//							} else {
//								count = billingPoint.getCount();
//							}
////							ServiceManager
////									.getManager()
////									.getPlayerItemsFromShopService()
////									.playerGetItem(player.getId(), billingPoint.getItemId(), -1, day, count, 20, order.getOrderNum(), 0, 0,
////											0);
//						}
//						order.setStatus(OrderDao.ORDER_STATUS_GRANT);
//					}
//					order.setPrice(billingPoint.getPrice());
//					ServiceManager.getManager().getOrderService().update(order);
//				}
//				if (order.getStatus() > OrderDao.ORDER_STATUS_BACK)
//					spbs.setHasGrant(1);
//				session.write(spbs);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		return null;
	}
}
