package com.app.dispatch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import com.app.dispatch.vo.ClientInfo;
import com.app.dispatch.vo.Hero;
import com.app.dispatch.vo.Map;
import com.app.dispatch.vo.Player;
import com.app.empire.protocol.data.syn.ReturnMove;
import com.app.empire.protocol.data.syn.ViewPlayerData;
import com.app.protocol.INetData;
import com.app.protocol.ProtocolManager;

/***
 * 地图同步服务
 * 
 * @author doter
 * 
 */
public class SyncService {
	private final int width = 450;// 区块宽
	private final int high = 250;// 区块高
	private HashMap<Integer, Map> mapConfig = new HashMap<Integer, Map>();
	private static final Logger log = Logger.getLogger(SyncService.class);
	/*** map　中区块的所有用户 地图id->块id->ioSession ****/
	private ConcurrentHashMap<Integer, ConcurrentHashMap<String, Vector<ClientInfo>>> mapPieceToPlayer = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, Vector<ClientInfo>>>();
	public SyncService() {
		try {
			this.loadMapConfig("map.txt");
			this.initMap();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/***
	 * 广播移动数据到此玩家附近的玩家
	 * 
	 * @param clientInfo
	 * @param toWidth
	 * @param toHigh
	 * @throws IllegalAccessException
	 */
	public void broadcastingMove(ClientInfo clientInfo, INetData data) throws IllegalAccessException {
		int id = data.readInt();// 玩家单位id（如英雄流水id
		byte direction = data.readByte();// 方向1-12
		int x = data.readInt();// 现在位置
		int y = data.readInt();// 现在位置
		int toX = data.readInt();// 所在宽度位置
		int toY = data.readInt();// 所在高度位置

		Player player = clientInfo.getPlayer();
		ArrayList<String> viewList = this.getPlayerView(player.getMapId(), x, y);// 获取视野范围
		ConcurrentHashMap<String, Vector<ClientInfo>> map = mapPieceToPlayer.get(player.getMapId());
		if (map != null) {
			ReturnMove move = new ReturnMove();
			move.setPlayerId(player.getPlayerId());
			move.setId(0);
			move.setDirection(direction);
			move.setToX(toX);
			move.setToY(toY);
			for (String string : viewList) {
				Vector<ClientInfo> vec = map.get(string);
				if (vec != null) {
					for (ClientInfo client : vec) {
						IoSession iosession = client.getIoSession();
						ProtocolManager.makeSegment(move).getPacketByteArray();
						iosession.write(ByteBuffer.wrap(ProtocolManager.makeSegment(move).getPacketByteArray()));
					}
				}
			}
		}
	}

	/***
	 * 客户端报告位置转发新视野
	 * 
	 * @param clientInfo
	 * @param nowWidth
	 * @param nowHigh
	 * @throws IllegalAccessException
	 */
	public void reportPlace(ClientInfo clientInfo, INetData data) throws IllegalAccessException {
		int heroId = data.readInt();// 英雄流水id
		byte nowDirection = data.readByte();// 方向1-12
		int nowX = data.readInt();// 所在宽度位置
		int nowY = data.readInt();// 所在高度位置
		Player player = clientInfo.getPlayer();
		int mapId = player.getMapId();
		Hero hero = player.getHero().get(heroId);

		hero.setDirection(nowDirection);
		hero.setX(nowX);
		hero.setY(nowY);

		ArrayList<String> oldViewList = this.getPlayerView(mapId, hero.getX(), hero.getY());// 获取视野范围
		ArrayList<String> newViewList = this.getPlayerView(mapId, nowX, nowY);// 新的视野范围
		ArrayList<String> addViewList = new ArrayList<String>();// 获取新增视野范围
		// ArrayList<String> delViewList = new ArrayList<String>();// 获取消失视野范围
		addViewList.addAll(newViewList);
		addViewList.removeAll(oldViewList);
		// delViewList.addAll(oldViewList);
		// delViewList.removeAll(newViewList);
		ConcurrentHashMap<String, Vector<ClientInfo>> map = mapPieceToPlayer.get(player.getMapId());
		if (map != null) {
			/* 告知我新视野的玩家 */
			ArrayList<Integer> playerId = new ArrayList<Integer>();
			ArrayList<Byte> direction = new ArrayList<Byte>();
			ArrayList<Integer> playersWidth = new ArrayList<Integer>();
			ArrayList<Integer> playersHigh = new ArrayList<Integer>();
			ArrayList<String> nickname = new ArrayList<String>();
			ArrayList<Integer> heroBaseId = new ArrayList<Integer>();
			boolean hasPlayer = false;
			/* 告知我新视野的玩家 */

			ReturnMove returnMove = new ReturnMove();
			returnMove.setPlayerId(player.getPlayerId());
			returnMove.setId(0);
			returnMove.setDirection(hero.getDirection());
			returnMove.setX(nowX);
			returnMove.setY(nowY);
			returnMove.setToX(hero.getToX());
			returnMove.setToY(hero.getToY());

			for (String string : addViewList) {
				Vector<ClientInfo> vec = map.get(string);
				if (vec != null) {
					for (ClientInfo client : vec) {
						IoSession iosession = client.getIoSession();
						/* 告知新视野玩家我的移动 */
						iosession.write(ByteBuffer.wrap(ProtocolManager.makeSegment(returnMove).getPacketByteArray()));
						Player player2 = client.getPlayer();
						playerId.add(player2.getPlayerId());
						direction.add(player2.getDirection());
						playersWidth.add(player2.getToX());
						playersHigh.add(player2.getY());
						nickname.add(player2.getNickname());
						heroBaseId.add(0);
						hasPlayer = true;
					}
				}
			}

			if (hasPlayer) {
				ViewPlayerData vpd = new ViewPlayerData();
				vpd.setPlayerId(ArrayUtils.toPrimitive(playerId.toArray(new Integer[playerId.size()])));
				vpd.setDirection(ArrayUtils.toPrimitive(direction.toArray(new Byte[direction.size()])));
				vpd.setWidth(ArrayUtils.toPrimitive(playersWidth.toArray(new Integer[playersWidth.size()])));
				vpd.setHigh(ArrayUtils.toPrimitive(playersHigh.toArray(new Integer[playersHigh.size()])));
				vpd.setNickname(nickname.toArray(new String[nickname.size()]));
				vpd.setHeroBaseId(ArrayUtils.toPrimitive(heroBaseId.toArray(new Integer[heroBaseId.size()])));
				IoSession iosession = clientInfo.getIoSession();
				iosession.write(ByteBuffer.wrap(ProtocolManager.makeSegment(vpd).getPacketByteArray()));
			}

			// 说明移动到了新的区块中
			if (addViewList.size() > 0) {
				// map 区块中删除用户
				this.delPlayerSessionToMapPiece(mapId, hero.getX(), hero.getY(), clientInfo);
				// 把用户增加到地图所在的块中
				this.addPlayerSessionToMapPiece(mapId, nowX, nowY, clientInfo, false);
			}
		}
	}

	/**
	 * 广播玩家属性等数据到附近的玩家
	 */
	public void broadcastingProperty(ClientInfo clientInfo) {
	}

	/*** 跳地图 */
	public void jumpMap(ClientInfo clientInfo, int mapId, int width, int high) {
		Player player = clientInfo.getPlayer();
		// Hero mainHero = player.getHero().get(player.getHeroId());

		// 随机生产着落点
		if (width == 0 && high == 0) {
			width = (int) (Math.random() * 500);
			high = (int) (Math.random() * 300);
		}
		ConcurrentHashMap<String, Vector<ClientInfo>> map = this.mapPieceToPlayer.get(mapId);
		/* 把周围的玩家发给我，把我的数据发给周围的玩家 */
		if (map != null) {
			/* 把视野范围内的玩家数据发送给我 */
			ArrayList<String> viewList = this.getPlayerView(mapId, width, high);// 新的视野范围(九宫格
			ArrayList<Integer> playerId = new ArrayList<Integer>();
			ArrayList<Byte> direction = new ArrayList<Byte>();
			ArrayList<Integer> playersWidth = new ArrayList<Integer>();
			ArrayList<Integer> playersHigh = new ArrayList<Integer>();
			ArrayList<String> nickname = new ArrayList<String>();
			ArrayList<Integer> heroBaseId = new ArrayList<Integer>();
			boolean hasPlayer = false;

			ViewPlayerData myvpd = new ViewPlayerData();
			myvpd.setPlayerId(new int[]{player.getPlayerId()});
			myvpd.setDirection(new byte[]{player.getDirection()});
			myvpd.setWidth(new int[]{player.getX()});
			myvpd.setHigh(new int[]{player.getY()});
			myvpd.setNickname(new String[]{player.getNickname()});
			myvpd.setHeroBaseId(new int[]{0});

			for (String string : viewList) {
				Vector<ClientInfo> vecs = map.get(string);
				if (vecs != null) {
					for (ClientInfo client : vecs) {
						IoSession iosession = client.getIoSession();
						/* 告知新视野玩家我的移动 */
						iosession.write(ByteBuffer.wrap(ProtocolManager.makeSegment(myvpd).getPacketByteArray()));

						Player p = client.getPlayer();
						playerId.add(p.getPlayerId());
						direction.add(p.getDirection());
						playersWidth.add(p.getX());
						playersHigh.add(p.getY());
						nickname.add(p.getNickname());
						heroBaseId.add(0);
						hasPlayer = true;
					}
				}
			}

			if (hasPlayer) {
				ViewPlayerData vpd = new ViewPlayerData();
				vpd.setPlayerId(ArrayUtils.toPrimitive(playerId.toArray(new Integer[playerId.size()])));
				vpd.setDirection(ArrayUtils.toPrimitive(direction.toArray(new Byte[direction.size()])));
				vpd.setWidth(ArrayUtils.toPrimitive(playersWidth.toArray(new Integer[playersWidth.size()])));
				vpd.setHigh(ArrayUtils.toPrimitive(playersHigh.toArray(new Integer[playersHigh.size()])));
				vpd.setNickname(nickname.toArray(new String[nickname.size()]));
				vpd.setHeroBaseId(ArrayUtils.toPrimitive(heroBaseId.toArray(new Integer[heroBaseId.size()])));
				// Channel channel = clientInfo.getChannel();
				IoSession iosession = clientInfo.getIoSession();
				iosession.write(ByteBuffer.wrap(ProtocolManager.makeSegment(vpd).getPacketByteArray()));
			}
		}

		// map 区块中删除用户
		this.delPlayerSessionToMapPiece(player.getMapId(), player.getX(), player.getY(), clientInfo);
		// 把用户增加到地图所在的块中
		this.addPlayerSessionToMapPiece(mapId, width, high, clientInfo, true);

		player.setMapId(mapId);
		player.setX(width);
		player.setY(high);
	}

	/***
	 * map 区块中添加用户
	 * 
	 * @param ioSession
	 */
	public void addPlayerSessionToMapPiece(Integer mapId, int width, int high, ClientInfo clientInfo, boolean isBroadcast) {
		Player player = clientInfo.getPlayer();
		// Hero mainHero = player.getHero().get(player.getHeroId());

		player.setX(width);
		player.setY(high);
		int widthNum = (int) Math.ceil((double) width / this.width);// 玩家宽度格子数
		int highNum = (int) Math.ceil((double) high / this.high);// 玩家高度格子数
		ConcurrentHashMap<String, Vector<ClientInfo>> map = this.mapPieceToPlayer.get(mapId);
		if (map != null) {
			/* 将用户添加到块中 */
			Vector<ClientInfo> vec = map.get(widthNum + "-" + highNum);
			if (vec != null) {
				vec.add(clientInfo);
			} else {
				Vector<ClientInfo> newVec = new Vector<ClientInfo>();
				newVec.add(clientInfo);
				map.put(widthNum + "-" + highNum, newVec);
			}
		} else {// 地图中第一个用户
			ConcurrentHashMap<String, Vector<ClientInfo>> newMap = new ConcurrentHashMap<String, Vector<ClientInfo>>();
			Vector<ClientInfo> vec = new Vector<ClientInfo>();
			vec.add(clientInfo);
			newMap.put(widthNum + "-" + highNum, vec);
			this.mapPieceToPlayer.put(mapId, newMap);
		}
	}

	/***
	 * map 区块中删除用户
	 * 
	 * @param ioSession
	 */
	public void delPlayerSessionToMapPiece(Integer mapId, int width, int high, ClientInfo clientInfo) {
		ConcurrentHashMap<String, Vector<ClientInfo>> map = this.mapPieceToPlayer.get(mapId);
		if (map != null) {
			int widthNum = (int) Math.ceil((double) width / this.width);// 玩家宽度格子数
			int highNum = (int) Math.ceil((double) high / this.high);// 玩家高度格子数
			Vector<ClientInfo> vec = map.get(widthNum + "-" + highNum);
			if (vec != null) {
				vec.remove(clientInfo);
			}
		}
	}

	/****
	 * 获取玩家所在的九宫格de 视野范围
	 * 
	 * @param width 玩家所在的高度
	 * @param high 　　玩家所在的宽度
	 * @return
	 */
	public ArrayList<String> getPlayerView(int mapId, int width, int high) {
		ArrayList<String> view = new ArrayList<String>();
		int widthNum = 0;
		int highNum = 0;
		if (width > 0)
			widthNum = (int) Math.ceil((double) width / this.width);// 玩家宽度格子数
		else
			widthNum = (int) -Math.ceil(Math.abs((double) width / this.width));
		if (high > 0)
			highNum = (int) Math.ceil((double) high / this.high);// 玩家高度格子数
		else
			highNum = (int) -Math.ceil(Math.abs((double) high / this.high));

		System.out.println(widthNum + ":" + highNum);
		Map map = mapConfig.get(mapId);
		if (map == null) {
			return view;
		}
		int mapWidthNum = map.getWidthNum();
		int mapHighNum = map.getHighNum();
		int i = -1;
		int rWidth = 0;
		int rhigh = 0;
		for (int x = 0; x < 3; x++) {
			rWidth = x + i + widthNum;
			if (Math.abs(rWidth) > mapWidthNum)
				continue;
			for (int y = 0; y < 3; y++) {
				rhigh = y + i + highNum;
				if (Math.abs(rhigh) > mapHighNum)
					continue;
				view.add(rWidth + ":" + rhigh);
			}
		}
		return view;
	}

	/***
	 * 加载地图配置
	 * 
	 * @param fname
	 * @throws Exception
	 */
	private void loadMapConfig(String fname) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(Thread.currentThread().getContextClassLoader().getResource(fname).getPath()));
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			if (line.startsWith("#"))
				continue;
			String secs[] = line.split("-");
			if (secs.length == 4) {
				Map map = new Map();
				map.setId(Integer.valueOf(secs[0]));
				map.setWidth(Integer.valueOf(secs[1]));
				map.setHigh(Integer.valueOf(secs[2]));
				map.setMaxPlayer(Integer.valueOf(secs[3]));
				mapConfig.put(map.getId(), map);
			}
		}
		reader.close();
	}
	/***
	 * 初始化地图（分块）
	 */
	private void initMap() {
		for (Entry<Integer, Map> entry : mapConfig.entrySet()) {
			Map mapObj = entry.getValue();
			int mapWidth = mapObj.getWidth();
			int mapHigh = mapObj.getHigh();
			int widthNum = (int) Math.ceil((double) mapWidth / this.width);// 宽度格子数
			int highNum = (int) Math.ceil((double) mapHigh / this.high);// 高度格子数
			mapObj.setWidthNum(widthNum);
			mapObj.setHighNum(highNum);
		}
	}

	public static void main(String[] args) {
		SyncService sync = new SyncService();
		ArrayList<String> arr = sync.getPlayerView(1, 8000, 8000);
		System.out.println(arr);
		System.out.println(-Math.ceil(Math.abs(-0.5)));
	}

}
