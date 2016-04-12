package com.app.empire.world.server.handler.npc;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.npc.GetNpcOK;
import com.app.empire.world.common.util.DateUtil;
import com.app.empire.world.entity.mongo.Npc;
import com.app.empire.world.entity.mongo.PlayerNpc;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;
/**
 * 获取npc
 * 
 * @author doter
 * 
 */
public class GetNpcHandler implements IDataHandler {
	private Logger log = Logger.getLogger(GetNpcHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		PlayerNpc playerNpc = ServiceManager.getManager().getPlayerNpcService().getPlayerNpc(worldPlayer);
		Map<Integer, Npc> npc = playerNpc.getNpc();
		int size = npc.size();
		int[] npcType = new int[size];// npc 类型 1 金币npc ，2钻石npc
		int[] lv = new int[size];// 升级成功后的等级
		long[] upgradeTime = new long[size];// 升级时间
		long[] receiveTime = new long[size];// 领取时间
		int[] buyCount = new int[size];// 当天购买次数，日期->次数
		int i = 0;
		for (Entry<Integer, Npc> entry : npc.entrySet()) {

			Npc npc2 = entry.getValue();
			Date getReceiveTime = npc2.getReceiveTime();
			Date getupgradeTime = npc2.getUpgradeTime();

			npcType[i] = npc2.getNpcType();
			lv[i] = npc2.getLv();
			upgradeTime[i] = getupgradeTime == null ? 0 : getupgradeTime.getTime();
			receiveTime[i] = getReceiveTime == null ? 0 : getReceiveTime.getTime();
			Map<String, Integer> buyCountMap = npc2.getBuyCount();
			String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
			if (buyCountMap != null && buyCountMap.containsKey(dateStr))
				buyCount[i] = buyCountMap.get(dateStr);
			else
				buyCount[i] = 0;
			i++;
		}
		GetNpcOK ok = new GetNpcOK(data.getSessionId(), data.getSerial());
		ok.setNpcType(npcType);
		ok.setLv(lv);
		ok.setUpgradeTime(upgradeTime);
		ok.setReceiveTime(receiveTime);
		ok.setBuyCount(buyCount);
		return ok;
	}
}
