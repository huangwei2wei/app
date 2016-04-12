package com.app.empire.world.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.app.empire.protocol.data.chat.ReceiveMessage;
import com.app.empire.protocol.data.server.BroadCast;
import com.app.empire.world.common.util.Common;
import com.app.empire.world.common.util.DateUtil;
import com.app.empire.world.dao.mongo.impl.BulletinDao;
import com.app.empire.world.entity.mongo.Bulletin;
import com.app.empire.world.exception.TipMessages;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.protocol.ProtocolManager;

/**
 * 公告服务
 * 
 * @author doter
 */
@Service
public class BulletinService {
	Logger log = Logger.getLogger(BulletinService.class);
	private ConcurrentHashMap<Integer, Bulletin> bulletins = new ConcurrentHashMap<Integer, Bulletin>();

	@Autowired
	BulletinDao bulletinDao;
	/**
	 * 加载公告
	 */
	public void load() {
		bulletins.clear();
		List<Bulletin> bulletinList = bulletinDao.getBulletins();
		for (Bulletin bulletin : bulletinList) {
			bulletins.put(bulletin.getId(), bulletin);
		}
	}
	/**
	 * 公告分发处理20秒一次
	 */
	public void processBulletin() {
		// System.out.println("processBulletinTask-----------");
		for (Entry<Integer, Bulletin> entry : bulletins.entrySet()) {
			Bulletin bulletin = entry.getValue();
			Date startTime = bulletin.getStartTime();// 开始时间
			Date endTime = bulletin.getEndTime();// 结束时间
			Date now = new Date();
			if (now.getTime() > startTime.getTime() && now.getTime() < endTime.getTime()) {
				int interval = bulletin.getInterval();// 间隔时间（秒）
				Date sendTime = bulletin.getSendTime();// 上次发送时间
				long i = now.getTime() - sendTime.getTime();
				if (i / 1000 > interval) {// 应该发送了
					String msg = bulletin.getMessage();
					sendBulletinToWorld(msg);
					bulletin.setSendTime(now);
					bulletinDao.save(bulletin);
				}
			}
		}
	}

	/**
	 * 发送公告信息给所有在线玩家
	 * 
	 * @param content 公告内容
	 * @param receiveName 公告发送人名称
	 * @param isColor 是否彩色公告
	 */
	public void sendBulletinToWorld(String content) {
		ReceiveMessage receiveMessage = new ReceiveMessage();
		receiveMessage.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		receiveMessage.setChatType(1);
		receiveMessage.setChatSubType(1);
		receiveMessage.setMessage(content.replace("\r", "").replace("\n", ""));
		receiveMessage.setSendId(0);
		receiveMessage.setSendName(TipMessages.SYSNAME_MESSAGE);
		receiveMessage.setReveId(0);
		receiveMessage.setReveName("");
		receiveMessage.setChannelId(Common.CHAT_SYSTEM);

		BroadCast broadCast = new BroadCast();
		broadCast.setChannel(Common.CHAT_WORLD_CHANNEL);
		broadCast.setData(ProtocolManager.makeSegment(receiveMessage).getPacketByteArray());
		ServiceManager.getManager().getConnectService().broadcast(broadCast);
	}
	/**
	 * GM获取公告列表
	 * 
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map getBulletinList(int id, int page, int pageSize) {
		Map mapData = new HashMap();
		Page<Bulletin> bulletins = bulletinDao.getBulletinList(id, page, pageSize);
		long total = bulletins.getTotalElements();
		ArrayList<Bulletin> playerList = new ArrayList<Bulletin>();
		for (Bulletin bulletin : bulletins) {
			playerList.add(bulletin);
		}
		mapData.put("total", total);
		mapData.put("list", playerList);
		return mapData;
	}

	/**
	 * 添加一条公告
	 * 
	 * @param message
	 * @param startTime
	 * @param endTime
	 * @param intervalTime
	 */
	public void addBulletin(String message, Date startTime, Date endTime, int intervalTime) {
		Bulletin bulletin = new Bulletin();
		bulletin.setMessage(message);
		bulletin.setStartTime(startTime);
		bulletin.setEndTime(endTime);
		bulletin.setInterval(intervalTime);
		bulletinDao.insert(bulletin);
	}
	/**
	 * 修改一条公告
	 * 
	 * @param message
	 * @param startTime
	 * @param endTime
	 * @param intervalTime
	 */
	public void updateBulletin(int id, String message, Date startTime, Date endTime, int intervalTime) {
		Bulletin bulletin = new Bulletin();
		bulletin.setId(id);
		bulletin.setMessage(message);
		bulletin.setStartTime(startTime);
		bulletin.setEndTime(endTime);
		bulletin.setInterval(intervalTime);
		bulletinDao.save(bulletin);
	}
	/**
	 * 删除公告
	 * 
	 * @param id
	 */
	public void delBulletin(int id) {
		bulletinDao.delete(id);
	}

}
