package com.app.empire.world.service.base.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.app.empire.world.WorldServer;
import com.app.empire.world.common.util.CryptionUtil;
import com.app.empire.world.common.util.DateUtil;
import com.app.empire.world.common.util.HttpClientUtil;
import com.app.empire.world.service.factory.ServiceManager;
/**
 * 检查充值服务
 * @author doter
 *
 */
public class CheckRechargeService {
	public String postUrl;

	private Logger log = Logger.getLogger(CheckRechargeService.class);

	public void addIntegralInfo(List<Object> list) throws InterruptedException {
		ServiceManager.getManager().getHttpThreadPool().execute(createTask(list));
	}

	private Runnable createTask(List<Object> list) {
		return new CheckRechargeThread(list);
	}

	public class CheckRechargeThread implements Runnable {
		private List<PlayerBillVo> pbList = new ArrayList<PlayerBillVo>();

		public CheckRechargeThread(List<Object> list) {
			String area = WorldServer.serverConfig.getAreaId();
			area = area.split("_")[0];

			Object[] pb;
			for (int i = 0; i < list.size(); i++) {
				try {
					pb = (Object[]) list.get(i);
					PlayerBillVo pbv = new PlayerBillVo();
					pbv.setAmount(Integer.parseInt(pb[0].toString()));
					pbv.setChannelId(pb[1].toString());
					pbv.setChargePrice(Float.parseFloat(pb[2].toString()));
					pbv.setCreateTime(DateUtil.parse(pb[3].toString(),""));
					pbv.setOrderNum(pb[4].toString());
					pbv.setPlayerId(Integer.parseInt(pb[5].toString()));
					pbv.setRemark(pb[6].toString());
					pbv.setArea(area);
					pbv.setAreaId(pb[7].toString());
					pbList.add(pbv);
				} catch (Exception e) {
					log.error(e);
				}
			}
		}

		public void run() {
			try {
				if (null == postUrl) {
					postUrl = ServiceManager.getManager().getConfiguration().getString("exchangeurl");
					if (null == postUrl)
						return;
				}
				String url;
				byte[] data;
				url = postUrl + "/checkRecharge";
				data = CryptionUtil.Encrypt(JSONArray.fromObject(pbList).toString(), ServiceManager.getManager().getConfiguration()
						.getString("deckey"));
				HttpClientUtil.PostData(url, data);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e, e);
			}
		}
	}

	public class PlayerBillVo {
		private Integer playerId;
		private Date createTime;
		private Integer amount;
		private String remark;
		private Float chargePrice;
		private String orderNum;
		private String channelId;
		private String area;
		private String areaId;

		public Integer getPlayerId() {
			return playerId;
		}

		public void setPlayerId(Integer playerId) {
			this.playerId = playerId;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public Integer getAmount() {
			return amount;
		}

		public void setAmount(Integer amount) {
			this.amount = amount;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public Float getChargePrice() {
			return chargePrice;
		}

		public void setChargePrice(Float chargePrice) {
			this.chargePrice = chargePrice;
		}

		public String getOrderNum() {
			return orderNum;
		}

		public void setOrderNum(String orderNum) {
			this.orderNum = orderNum;
		}

		public String getChannelId() {
			return channelId;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public String getAreaId() {
			return areaId;
		}

		public void setAreaId(String areaId) {
			this.areaId = areaId;
		}

	}
}
