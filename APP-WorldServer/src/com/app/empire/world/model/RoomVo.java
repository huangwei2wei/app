package com.app.empire.world.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang.ArrayUtils;
import com.app.empire.protocol.data.chat.SyncChannels;
import com.app.empire.protocol.data.pvproom.RoomPlayerInfo;
import com.app.empire.protocol.data.server.SyncPlayer;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;

/**
 * pvp 房间
 * 
 * @author doter
 * 
 */
public class RoomVo {
	private Date createTime;// 创建时间
	private Date startTime;// 开始时间
	private int roomId;// 房间号
	private int homeowners;// 房主 playerId
	private int status;// 状态 1、房主点击开始，2 、所有玩家已经就绪进入战斗
	private List<Integer> completePlayerIds;// 进入战斗界面后加载完成的玩家列表
	private HashMap<Integer, HashMap<Integer, RoomHeroEntitys>> heroMap = new HashMap<Integer, HashMap<Integer, RoomHeroEntitys>>();// 角色id->英雄id->出战英雄
	private HashMap<Integer, WorldPlayer> worldPlayerMap = new HashMap<Integer, WorldPlayer>();// 角色id -> 角色数据

	public RoomVo(WorldPlayer worldPlayer, PlayerHero playerHero, int roomId) {
		this.roomId = roomId;
		this.createTime = new Date();
		this.homeowners = worldPlayer.getPlayer().getId();
		RoomHeroEntitys entitys = new RoomHeroEntitys(1);
		entitys.setPlayerHero(playerHero);
		entitys.setHp(playerHero.getHp());
		this.addPlayer(worldPlayer, playerHero);
	}
	public int getRoomId() {
		return roomId;
	}
	/** 添加玩家英雄 返回玩家数量 */
	public int addPlayer(WorldPlayer worldPlayer, PlayerHero playerHero) {
		RoomHeroEntitys entitys = new RoomHeroEntitys(1);
		entitys.setPlayerHero(playerHero);
		entitys.setHp(playerHero.getHp());

		int playerId = worldPlayer.getPlayer().getId();
		int hreoId = playerHero.getId();
		if (this.heroMap.containsKey(playerId)) {
			HashMap<Integer, RoomHeroEntitys> playerHeroMap = this.heroMap.get(playerId);
			playerHeroMap.put(hreoId, entitys);
		} else {
			HashMap<Integer, RoomHeroEntitys> playerHeroMap = new HashMap<Integer, RoomHeroEntitys>();
			playerHeroMap.put(hreoId, entitys);
			this.heroMap.put(playerId, playerHeroMap);
		}
		this.worldPlayerMap.put(playerId, worldPlayer);
		return worldPlayerMap.size();
	}

	/** 获取房间人数 */
	public int getPlayerCount() {
		return worldPlayerMap.size();
	}

	/** 删除玩家 返回房间人数 */
	public int movePlayer(WorldPlayer worldPlayer) {
		int playerId = worldPlayer.getPlayer().getId();
		this.heroMap.remove(playerId);
		this.worldPlayerMap.remove(playerId);
		return worldPlayerMap.size();
	}

	/** 房间广播 */
	public void broadcast(AbstractData data) {
		for (WorldPlayer worldPlayer : worldPlayerMap.values()) {
			worldPlayer.sendData(data);
		}
		// BroadCast broadCast = new BroadCast();
		// broadCast.setChannel(Common.PVP_ROOM + "_" + roomId);
		// broadCast.setData(ProtocolManager.makeSegment(data).getPacketByteArray());
		// this.connectSession.write(broadCast);
	}
	/** 广播房间内的玩家列表数据给其他玩家 **/
	public void broadcastRoomPlayer() {
		RoomPlayerInfo roomPlayerInfo = new RoomPlayerInfo();
		ArrayList<Integer> playerIds = new ArrayList<Integer>();// 玩家角色id
		ArrayList<String> nickNames = new ArrayList<String>();// 玩家角色id
		ArrayList<Integer> heroIds = new ArrayList<Integer>();// 玩家角色id
		ArrayList<Integer> heroExtIds = new ArrayList<Integer>();// 玩家角色id
		for (Entry<Integer, WorldPlayer> entry : worldPlayerMap.entrySet()) {
			Integer playerId = entry.getKey();
			WorldPlayer worldPlayer = entry.getValue();
			String nickName = worldPlayer.getPlayer().getNickname();
			HashMap<Integer, RoomHeroEntitys> heros = heroMap.get(playerId);
			for (RoomHeroEntitys entitys : heros.values()) {
				PlayerHero playerHero = entitys.getPlayerHero();
				playerIds.add(playerId);
				nickNames.add(nickName);
				heroIds.add(playerHero.getId());
				heroExtIds.add(playerHero.getHeroExtId());
			}
		}
		roomPlayerInfo.setPlayerId((ArrayUtils.toPrimitive(playerIds.toArray(new Integer[playerIds.size()]))));
		roomPlayerInfo.setNickName(nickNames.toArray(new String[nickNames.size()]));
		roomPlayerInfo.setHeroId(ArrayUtils.toPrimitive(heroIds.toArray(new Integer[heroIds.size()])));
		roomPlayerInfo.setHeroExtId(ArrayUtils.toPrimitive(heroExtIds.toArray(new Integer[heroExtIds.size()])));
		this.broadcast(roomPlayerInfo);
	}

	/**
	 * 获取房间内的玩家英雄
	 * 
	 * @param playerId
	 * @param heroId
	 * @return
	 */
	public RoomHeroEntitys getPlayerHero(int playerId, int heroId) {
		HashMap<Integer, RoomHeroEntitys> heros = heroMap.get(playerId);
		if (heros == null)
			return null;
		RoomHeroEntitys e = heros.get(heroId);
		return e;
	}

	/**
	 * 添加一个加载完成的玩家
	 * 
	 * @param worldPlayer
	 * @return 返回加重完成的人数
	 */
	public int addCompletePlayer(WorldPlayer worldPlayer) {
		Integer playerId = worldPlayer.getPlayer().getId();
		if (!completePlayerIds.contains(playerId))
			completePlayerIds.add(playerId);
		return completePlayerIds.size();
	}

	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public int getHomeowners() {
		return homeowners;
	}
	public void setHomeowners(int homeowners) {
		this.homeowners = homeowners;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/** 同步玩家频道设置 */
	public void syncChannels(String[] addChannels, String[] removeChannels) {
		for (Entry<Integer, WorldPlayer> entry : worldPlayerMap.entrySet()) {
			WorldPlayer worldPlayer = entry.getValue();
			ConnectSession session = worldPlayer.getConnectSession();
			if (session == null)
				continue;

			int playerId = worldPlayer.getPlayer().getId();
			SyncChannels sync = new SyncChannels();
			sync.setToSession(session.getPlayerSessionId(playerId));
			sync.setAdd(addChannels);
			sync.setRemove(removeChannels);
			session.write(sync);
		}
	}

	/** 同步房间的玩家数据至dis */
	public void syncPlayer() {
		for (Entry<Integer, WorldPlayer> entry : worldPlayerMap.entrySet()) {
			WorldPlayer worldPlayer = entry.getValue();
			HashMap<Integer, RoomHeroEntitys> entitys = heroMap.get(worldPlayer.getPlayer().getId());

			ConnectSession session = worldPlayer.getConnectSession();
			for (RoomHeroEntitys entryPlayerHero : entitys.values()) {
				PlayerHero playerHero = entryPlayerHero.getPlayerHero();
				Player player = worldPlayer.getPlayer();
				SyncPlayer syncPlayer = new SyncPlayer();
				syncPlayer.setId(player.getId());
				syncPlayer.setNickname(player.getNickname());
				syncPlayer.setLv(player.getLv());
				syncPlayer.setVipLv(player.getVipLv());
				syncPlayer.setPlayerPro(player.getProperty());
				syncPlayer.setPlayerFight(player.getFight());
				syncPlayer.setRoomId(roomId);
				syncPlayer.setHeroId(playerHero.getId());
				syncPlayer.setHeroHp(playerHero.getHp());
				syncPlayer.setHeroPro(playerHero.getProperty());
				syncPlayer.setHeroFight(playerHero.getFight());
				session.write(syncPlayer);
			}
		}
	}
}
