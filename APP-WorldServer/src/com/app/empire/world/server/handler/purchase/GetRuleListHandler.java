package com.app.empire.world.server.handler.purchase;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.purchase.GetRuleListOk;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取兑换比率列表
 * 
 * @author Administrator
 */
public class GetRuleListHandler implements IDataHandler {
	Logger log = Logger.getLogger(GetRuleListHandler.class);

	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		try {
			int[] price = new int[4];
			int[] ratio = new int[5];

			price[0] = 10;
			price[1] = 50;
			price[2] = 100;
			price[3] = 300;

			ratio[0] = 1000;
			ratio[1] = 1100;
			ratio[2] = 1150;
			ratio[3] = 1200;
			ratio[4] = 1250;

			GetRuleListOk getRuleListOk = new GetRuleListOk(data.getSessionId(), data.getSerial());
			getRuleListOk.setPrice(price);
			getRuleListOk.setRatio(ratio);
			session.write(getRuleListOk);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
