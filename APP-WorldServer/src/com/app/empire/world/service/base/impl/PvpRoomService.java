package com.app.empire.world.service.base.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.app.empire.protocol.data.pvproom.StartStatus;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.RoomVo;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;

/**
 * pvp 房间管理服务
 * 
 * @author doter
 * 
 */

@Service
public class PvpRoomService {
	private static final int ROOM_MAX_PLAYER = 5;// 房间最大人数
	private Logger log = Logger.getLogger(PvpRoomService.class);
	private AtomicInteger applyRoomId = new AtomicInteger(0);
	private HashMap<Integer, HashMap<Integer, RoomVo>> roomPool = new HashMap<Integer, HashMap<Integer, RoomVo>>();// 临时房间匹配池,, 类型id->房间id->room,1、神兽副本
	public PvpRoomService() {
		new Thread(new clearRoomTask()).start();
	}
	/**
	 * 获取房间列表
	 * 
	 * @param roomType 1、神兽副本
	 * @return
	 */
	public List<RoomVo> getRoomList(int roomType) {
		ArrayList<RoomVo> roomList = new ArrayList<RoomVo>();
		HashMap<Integer, RoomVo> rooms = roomPool.get(roomType);
		if (rooms != null) {
			for (Entry<Integer, RoomVo> entry : rooms.entrySet()) {
				RoomVo room = entry.getValue();
				Date startTime = room.getStartTime();
				if (startTime == null)
					roomList.add(room);
			}
		}
		return roomList;
	}

	/**
	 * 创建房间
	 * 
	 * @param worldPlayer
	 * @param heroId
	 * @param roomType
	 * @return
	 */
	public RoomVo createRoom(WorldPlayer worldPlayer, int heroId, int roomType) throws PlayerDataException {
		PlayerHero playerHero = ServiceManager.getManager().getPlayerHeroService().getHeroByHeroId(worldPlayer, heroId);
		if (playerHero == null) {
			GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
			throw new PlayerDataException(gameConfigService.getMsg(10));// 英雄不存在
		}
		RoomVo room = new RoomVo(worldPlayer, playerHero, this.applyRoomId());
		HashMap<Integer, RoomVo> rooms = roomPool.get(roomType);
		if (rooms == null) {
			rooms = new HashMap<Integer, RoomVo>();
			roomPool.put(roomType, rooms);
		}
		rooms.put(room.getRoomId(), room);
		log.info("创建房间,id：" + room.getRoomId());
		return room;
	}

	/**
	 * 玩家进入房间
	 * 
	 * @param worldPlayer
	 * @param heroId 英雄流水id
	 * @param roomType 房间类型
	 * @param roomId 房间id
	 * @return
	 * @throws PlayerDataException
	 */
	public RoomVo intoRoom(WorldPlayer worldPlayer, int heroId, int roomType, int roomId) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		PlayerHero playerHero = ServiceManager.getManager().getPlayerHeroService().getHeroByHeroId(worldPlayer, heroId);
		if (playerHero == null)
			throw new PlayerDataException(gameConfigService.getMsg(10));// 英雄不存在

		HashMap<Integer, RoomVo> rooms = roomPool.get(roomType);
		if (rooms == null)
			throw new PlayerDataException(gameConfigService.getMsg(32));// 房间不存在
		RoomVo room = rooms.get(roomId);
		if (room == null)
			throw new PlayerDataException(gameConfigService.getMsg(32));// 房间不存在
		if (room.getPlayerCount() >= ROOM_MAX_PLAYER)
			throw new PlayerDataException(gameConfigService.getMsg(34));// 房间人数已满

		room.addPlayer(worldPlayer, playerHero);
		room.broadcastRoomPlayer();// 广播房间内的玩家列表数据给其他玩家
		log.info("playerId:" + worldPlayer.getPlayer().getId() + "进入房间,roomId:" + roomId);
		return room;
	}

	/**
	 * 房主点击开始
	 * 
	 * @param worldPlayer
	 * @param roomType
	 * @param roomId
	 * @return
	 * @throws PlayerDataException
	 */
	public RoomVo start(WorldPlayer worldPlayer, int roomType, int roomId) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		HashMap<Integer, RoomVo> rooms = roomPool.get(roomType);
		if (rooms == null)
			throw new PlayerDataException(gameConfigService.getMsg(32));// 房间不存在
		RoomVo room = rooms.get(roomId);
		if (room == null)
			throw new PlayerDataException(gameConfigService.getMsg(32));// 房间不存在
		if (room.getHomeowners() != worldPlayer.getPlayer().getId())
			throw new PlayerDataException(gameConfigService.getMsg(33));// 只有房主才能开始
		// 将房间内的玩家加入该房间频道
		// room.syncChannels(new String[]{Common.PVP_ROOM + "_" + roomId}, new String[0]);
		// 同步房间玩家数据至dis
		// room.syncPlayer();

		StartStatus startStatus = new StartStatus();
		startStatus.setStatus(1);
		room.broadcast(startStatus);// 广播房间内玩家房间状态，房主已点击开始
		room.setStartTime(new Date());
		log.info("房主点击开始 roomId:" + roomId);
		return room;
	}

	/**
	 * 客户端加载完成
	 * 
	 * @param worldPlayer
	 * @param roomType 房间类型
	 * @param roomId 房间号
	 * @return
	 * @throws PlayerDataException
	 */
	public RoomVo start2(WorldPlayer worldPlayer, int roomType, int roomId) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		HashMap<Integer, RoomVo> rooms = roomPool.get(roomType);
		if (rooms == null)
			throw new PlayerDataException(gameConfigService.getMsg(32));// 房间不存在
		RoomVo room = rooms.get(roomId);
		if (room == null)
			throw new PlayerDataException(gameConfigService.getMsg(32));// 房间不存在
		int completePlayerCount = room.addCompletePlayer(worldPlayer);
		if (completePlayerCount >= room.getPlayerCount()) {// 所有玩家加载完成
			StartStatus startStatus = new StartStatus();
			startStatus.setStatus(2);
			room.broadcast(startStatus);// 广播房间内玩家房间状态，开始战斗
		}
		log.info("客户端加载完成 roomId:" + roomId + ",playerId:" + worldPlayer.getPlayer().getId());
		return room;
	}

	/**
	 * 退出房间
	 * 
	 * @param worldPlayer
	 * @return
	 */
	public RoomVo outRoom(WorldPlayer worldPlayer, int roomType, int roomId) {
		HashMap<Integer, RoomVo> rooms = roomPool.get(roomType);
		if (rooms == null)
			return null;
		RoomVo room = rooms.get(roomId);
		if (room == null)
			return null;
		int playerCount = room.movePlayer(worldPlayer);
		if (playerCount == 0) {// 房间空
			rooms.remove(roomId);
		} else {
			room.broadcastRoomPlayer();// 广播房间内的玩家列表数据给其他玩家
		}
		log.info("退出房间,roomId:" + roomId + ",playerId:" + worldPlayer.getPlayer().getId());
		return room;
	}

	/*** 结束 ***/
	public void endRoom(ConnectSession connectSession, int roomType, int roomId) {
		HashMap<Integer, RoomVo> rooms = roomPool.get(roomType);
		if (rooms == null)
			return;
		rooms.remove(roomId);
		// 移除频道
		// RemoveChannels removeChannels = new RemoveChannels();
		// removeChannels.setChannelsName(Common.PVP_ROOM + "_" + roomId);
		// connectSession.write(removeChannels);
		log.info("结束,roomId:" + roomId);
	}

	/**
	 * 广播房间内的数据
	 */
	public void broadcast(RoomVo room, AbstractData data) {
		if (room != null)
			room.broadcast(data);
	}

	/**
	 * 获取房间
	 * 
	 * @param roomType
	 * @param roomId
	 * @return
	 */
	public RoomVo getRoom(int roomType, int roomId) {
		HashMap<Integer, RoomVo> rooms = roomPool.get(roomType);
		if (rooms == null)
			return null;
		RoomVo room = rooms.get(roomId);
		return room;
	}

	/**
	 * 申请房间号（唯一性
	 * 
	 * @return
	 */
	private int applyRoomId() {
		int roomId = applyRoomId.incrementAndGet();
		if (roomId >= Integer.MAX_VALUE) {
			applyRoomId.set(0);
			roomId = applyRoomId.incrementAndGet();
		}
		return roomId;
	}

	/** 清除房间 */
	private void clearRoom() {
		// long nowTime = System.currentTimeMillis();
		// for (int i = 0; i < roomPool.size(); i++) {
		// Room room = roomPool.get(i);
		// long createTime = room.getCreateTime().getTime();
		// if (nowTime - createTime > 600000) {// 清除超过10分钟
		// roomPool.remove(i);
		// }
		// }
	}

	/** 清除房间线程 */
	public class clearRoomTask implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(300000);// 5 分钟
				PvpRoomService.this.clearRoom();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
