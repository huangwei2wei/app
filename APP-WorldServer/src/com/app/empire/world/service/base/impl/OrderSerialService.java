package com.app.empire.world.service.base.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.app.empire.world.service.factory.ServiceManager;
import com.app.protocol.data.AbstractData;

/**
 * @author zguoqiu
 * @version 创建时间：2013-8-17 下午4:55:10 类说明
 */
@Service
public class OrderSerialService {
	public String serialurl;
	public String appenname;
	private Logger log = Logger.getLogger(OrderSerialService.class);

	public void addSerialInfo(AbstractData data) {
		ServiceManager.getManager().getHttpThreadPool().execute(createTask(data));
	}

	private Runnable createTask(AbstractData data) {
		return new RequestSerialThread(data);
	}

	public class RequestSerialThread implements Runnable {
		private AbstractData data;

		public RequestSerialThread(AbstractData data) {
			this.data = data;
		}

		public void run() {
//			try {
//				if (null == serialurl) {
//					serialurl = ServiceManager.getManager().getConfiguration().getString("serialurl");
//					appenname = ServiceManager.getManager().getConfiguration().getString("appenname");
//				}
//				String url = serialurl + "/serial";
//				ConnectSession session = (ConnectSession) data.getHandlerSource();
//				RequestSmsCodeSerialid requestSmsCodeSerialid = (RequestSmsCodeSerialid) data;
//				WorldPlayer player = session.getPlayer(data.getSessionId());
//				BillingPoint billingPoint = ServiceManager.getManager().getOrderService()
//						.getBillingPointById(requestSmsCodeSerialid.getId());
//				if (null != billingPoint) {
//					SerialInfo serialInfo = new SerialInfo();
//					serialInfo.setAppId(appenname);
//					serialInfo.setPlayerId(player.getId() + "");
//					serialInfo.setServiceIp(ServiceManager.getManager().getConfiguration().getString("callbackip"));
//					serialInfo.setServicePort(ServiceManager.getManager().getConfiguration().getString("http"));
//					serialInfo.setChannel(requestSmsCodeSerialid.getChannel());
//					serialInfo.setPayChannel(requestSmsCodeSerialid.getPayChannel());
//					serialInfo.setPayAmt((int) billingPoint.getPrice());
//					JSONObject jsonObject = JSONObject.fromObject(serialInfo);
//					byte[] dataByte = CryptionUtil.Encrypt(jsonObject.toString(),
//							ServiceManager.getManager().getConfiguration().getString("deckey"));
//					String receipt = HttpClientUtil.PostData(url, dataByte);
//					// System.out.println(receipt);
//					if (!"fail".equals(receipt)) {
//						jsonObject = JSONObject.fromObject(receipt);
//						serialInfo = (SerialInfo) JSONObject.toBean(jsonObject, SerialInfo.class);
//						Order order = new Order();
//						order.setPointId(requestSmsCodeSerialid.getId());
//						order.setPlayerId(player.getId());
//						order.setSerialNum(serialInfo.getSerialNum());
//						order.setOrderNum(serialInfo.getOrderNum());
//						order.setCreateTime(new Date());
//						order.setUpdateTime(order.getCreateTime());
//						order.setStatus(OrderDao.ORDER_STATUS_INIT);
//						order.setRemark(requestSmsCodeSerialid.getLocalId() + "");
//						order.setChannel(requestSmsCodeSerialid.getChannel());
//						order.setPayChannel(requestSmsCodeSerialid.getPayChannel());
//						ServiceManager.getManager().getOrderService().save(order);
//						RequestSmsCodeSerialidOk requestSmsCodeSerialidOk = new RequestSmsCodeSerialidOk(data.getSessionId(),
//								data.getSerial());
//						requestSmsCodeSerialidOk.setSerialNum(serialInfo.getSerialNum());
//						requestSmsCodeSerialidOk.setId(requestSmsCodeSerialid.getId());
//						requestSmsCodeSerialidOk.setLocalId(requestSmsCodeSerialid.getLocalId());
//						session.write(requestSmsCodeSerialidOk);
//					} else {
//						data.setFlag((byte) -1);
//						session.write(data);
//					}
//				}
//			} catch (Exception e) {
//				log.error(e, e);
//			}
		}
	}
}
