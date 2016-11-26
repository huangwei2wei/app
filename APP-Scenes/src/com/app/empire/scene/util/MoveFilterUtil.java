//package com.app.empire.scene.util;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * <pre>
// * 移动过滤
// * </pre>
// */
//public class MoveFilterUtil {
//	public static Map<Integer, byte[][]> road = new HashMap<Integer, byte[][]>();
//	public static Map<String, PlayerMoveInfo> playerInfos = new ConcurrentHashMap<String, PlayerMoveInfo>();
//	public static final int MAX_SPEED = 25; // 移动最快速度--30/每格
//	public static final long LIFE = 1000 * 60 * 60; // 不再移动对象寿命
//	public static final int SUCCESS = 1;
//	public static final int ERROR_SPEED = 1001;
//	public static final int ERROR_NODE = 1002;
//	public static final int ERROR_CONNECT = 1003;
//
//	public static void main(String[] args) {
//		init();
//	}
//
//	public static boolean init() {
//		String configPath = Config.getPath("language.path");
//		configPath = configPath.substring(0, configPath.lastIndexOf("/")) + "/road";
//
//		File dirFile = new File(configPath);
//		if (!dirFile.exists() || !dirFile.isDirectory()) {
//			Log.error("地图文件不存在..........................");
//			return true;
//		}
//		File[] files = dirFile.listFiles();
//		if (files == null || files.length == 0) {
//			Log.error("地图文件不存在..........................");
//			return true;
//		}
//		for (File file : files) {
//			try {
//				readFile(file);
//			} catch (Exception e) {
//				Log.error("服务器启动时解析地图文件出错" + e);
//			}
//		}
//		// 用户管理
//		new Timer("MoveFilterUtil").schedule(new ManagerTask(), 0, LIFE / 4);
//		return true;
//	}
//
//	/**
//	 * <pre>
//	 * 跨服移动
//	 * </pre>
//	 * 
//	 * @param userId
//	 * @param serverName
//	 * @param mapId
//	 * @param moveNodes
//	 * @return
//	 */
//	public static int crossMoveCheck(int userId, String serverName, int mapId, short[][] moveNodes) {
//		return check(userId + "_" + serverName, mapId, moveNodes);
//	}
//
//	/** 移动检测 */
//	public static int check(String moveId, int mapId, short[][] moveNodes) {
//		try {
//			// 没有移动
//			if (moveNodes.length == 0) {
//				return SUCCESS;
//			}
//
//			int checkId = mapId; // 战场地图公用
//			if ((mapId >= 4001 && mapId <= 4011) || mapId == 5001 || mapId == 4501 || mapId == 10000 || mapId == 30000) { // 战场不检测
//				return SUCCESS;
//			}
//
//			if (!road.containsKey(checkId)) {
//				return SUCCESS;
//			}
//			PlayerMoveInfo pinfo = null;
//			if (!playerInfos.containsKey(moveId)) {
//				pinfo = new PlayerMoveInfo(moveId);
//				playerInfos.put(moveId, pinfo);
//			} else {
//				pinfo = playerInfos.get(moveId);
//				// 判断是否与上次，属同一张地图，如不同，则不要求与旧有节点连续
//				if (pinfo.getMapId() != mapId) {
//					pinfo.setMapId(mapId);
//					pinfo.setOlderNode(new short[] { -1, -1 });
//					pinfo.setOldTime(-1);
//				}
//			}
//			// 速度判断 ：加速
//			if (!speedCheck(pinfo.getOldTime(), moveNodes.length - 1)) {
//				return ERROR_SPEED;
//			}
//			// 节点是否连续 ：瞬移
//			if (!connectCheck(pinfo.getOlderNode(), moveNodes)) {
//				return ERROR_CONNECT;
//			}
//
//			// 如果存放地图，则验证节点是否合法
//			if (!nodeCheck(checkId, moveNodes)) {
//				return ERROR_NODE;
//			}
//
//			// 更新移动信息
//			pinfo.setMapId(mapId);
//			if (moveNodes.length > 1) { // 当节点只有1个时候，可能是刚进入某个节点边缘马上停下，导致速度判断异常
//				pinfo.setOldTime(System.currentTimeMillis());
//			}
//			pinfo.setOlderNode(moveNodes[moveNodes.length - 1]);
//			return SUCCESS;
//		} catch (Exception e) {
//			Log.info("移动校验异常" + e);
//		}
//		return SUCCESS;
//	}
//
//	/**
//	 * <pre>
//	 * 验证移动节点是否合法
//	 * </pre>
//	 */
//	private static boolean nodeCheck(int mapId, short[][] moveNodes) {
//		byte[][] nodes = road.get(mapId);
//		for (short[] node : moveNodes) {
//			if (node[0] >= nodes.length || node[1] >= nodes[node[0]].length) {
//				return false;
//			}
//			if (nodes[node[0]][node[1]] == 0) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * <pre>
//	 * 验证移动速度
//	 * 
//	 * <pre>
//	 */
//	private static boolean speedCheck(long oldTime, int nodesCount) {
//		if (oldTime == -1) {
//			return true;
//		}
//		boolean result = (System.currentTimeMillis() - oldTime) >= nodesCount * MAX_SPEED;;
//		if (!result) {
//
//		}
//		return result;
//	}
//
//	/**
//	 * <pre>
//	 * 验证移动速度于移动衔接度是否合法
//	 * </pre>
//	 */
//	private static boolean connectCheck(short[] oldNode, short[][] moveNodes) {
//
//		// 检测与上次移动，是否连续
//		if (oldNode[0] != -1 && oldNode[1] != -1) {
//			if (Math.abs(oldNode[0] - moveNodes[0][0]) > 5 || Math.abs(oldNode[1] - moveNodes[0][1]) > 5) { // 客户端修正，5个格子
//				return false;
//			}
//		}
//		// 检测本次移动是否连续
//		for (int i = 0; i < moveNodes.length - 1; i++) {
//			if (Math.abs(moveNodes[i][0] - moveNodes[i + 1][0]) > 1 || Math.abs(moveNodes[i][1] - moveNodes[i + 1][1]) > 1) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * <pre>
//	 * 解析地图文件
//	 * </pre>
//	 */
//	private static boolean readFile(File file) throws Exception {
//		// 获取地图ID
//		int mapId = 0;
//		try {
//			mapId = Integer.valueOf(file.getName().substring(0, file.getName().indexOf(".")));
//		} catch (Exception e) {
//			Log.error("地图名称不合法");
//		}
//		java.io.FileReader fin = new FileReader(file);
//		java.io.BufferedReader buf = new BufferedReader(fin);
//		StringBuffer sb = new StringBuffer();
//		String sublen = null;
//		while ((sublen = buf.readLine()) != null && !sublen.equals("")) {
//			sb.append(sublen);
//		}
//		String[] roadsStr = sb.toString().split(",");
//		// 获取所有可以移动节点
//		short[][] roads = new short[roadsStr.length][2];
//		for (int i = 0; i < roads.length; i++) {
//			String[] x = roadsStr[i].split("_");
//			if (x.length == 2) {
//				try {
//					roads[i][0] = Short.valueOf(x[0]);
//					roads[i][1] = Short.valueOf(x[1]);
//				} catch (Exception e) {
//					Log.error("文件格式错误 ：  filename: " + file.getName() + "--" + roadsStr[i]);
//					return false;
//				}
//			}
//		}
//
//		// 获取移动最大坐标值
//		short maxX = 0;
//		short maxY = 0;
//		for (int i = 0; i < roads.length - 1; i++) {
//			if (roads[i][0] > maxX) {
//				maxX = roads[i][0];
//			}
//			if (roads[i][1] > maxY) {
//				maxY = roads[i][1];
//			}
//		}
//		// 以移动最大坐标补齐数组
//		byte[][] map = new byte[maxX + 2][maxY + 2];
//		for (short[] node : roads) {
//			List<short[]> collected = compatibleNodeCheck(node); // 补齐可行走点附近点
//			for (short[] s : collected) {
//				map[s[0]][s[1]] = 1;
//			}
//		}
//		// 存放
//		road.put(mapId, map);
//		return true;
//	}
//
//	/**
//	 * <pre>
//	 * 客户端寻路机制优化兼容：可行走点邻点亦视为可行走
//	 * </pre>
//	 */
//	private static List<short[]> compatibleNodeCheck(short[] node) {
//		List<short[]> nodes = new ArrayList<short[]>();
//		for (short i = -1; i <= 1; i++) {
//			for (short j = -1; j <= 1; j++) {
//				if (node[0] + i >= 0 && node[1] + j >= 0) {
//					nodes.add(new short[] { (short) (node[0] + i), (short) (node[1] + j) });
//				}
//			}
//		}
//		return nodes;
//	}
//
//	public static short[] getCrossBackNode(int userId, String serverName) {
//		return getBackNode(userId + serverName);
//	}
//
//	/** 回退节点 */
//	public static short[] getBackNode(String movdId) {
//		if (playerInfos.containsKey(movdId)) {
//			return playerInfos.get(movdId).getOlderNode();
//		} else {
//			return new short[] { -1, -1 };
//		}
//	}
//
//	// 跨服
//	public static void crossReSendNode(int userId, String serverName, int x, int y) {
//		reSendNode(userId + "_" + serverName, x, y);
//	}
//
//	/** 服务器重定位节点 */
//	public static void reSendNode(String moveId, int x, int y) {
//		if (playerInfos.containsKey(moveId)) {
//			PlayerMoveInfo changer = playerInfos.get(moveId);
//			short[] nNode = new short[] { (short) x, (short) y };
//			changer.setOlderNode(nNode);
//			changer.setOldTime(-1);
//		}
//	}
//
//	/**
//	 * <pre>
//	 * 切换场景
//	 * </pre>
//	 */
//	public static void changeMap(String id) {
//		try {
//			if (playerInfos.containsKey(id)) {
//				playerInfos.get(id).setMapId(-1);
//			}
//		} catch (Exception e) {
//			Log.error("移动切换地图出错" + e);
//		}
//	}
//
//	/**
//	 * 玩家移动信息管理
//	 */
//	public static void infoManager() {
//		long now = System.currentTimeMillis();
//		for (PlayerMoveInfo p : playerInfos.values()) {
//			if ((now - p.getOldTime()) > LIFE) {
//				playerInfos.remove(p.getId());
//			}
//		}
//	}
//
//	public static class PlayerMoveInfo {
//		private String id;
//		private int mapId;
//		private short[] olderNode;
//		private long oldTime;
//
//		public PlayerMoveInfo(String id) {
//			this.id = id;
//			mapId = -1;
//			oldTime = -1;
//			olderNode = new short[] { -1, -1 };
//		}
//
//		public String getId() {
//			return id;
//		}
//
//		public void setId(String id) {
//			this.id = id;
//		}
//
//		public int getMapId() {
//			return mapId;
//		}
//
//		public void setMapId(int mapId) {
//			this.mapId = mapId;
//		}
//
//		public short[] getOlderNode() {
//			return olderNode;
//		}
//
//		public void setOlderNode(short[] olderNode) {
//			this.olderNode = olderNode;
//		}
//
//		public long getOldTime() {
//			return oldTime;
//		}
//
//		public void setOldTime(long oldTime) {
//			this.oldTime = oldTime;
//		}
//	}
//
//	static class ManagerTask extends TimerTask {
//		public ManagerTask() {
//			super();
//		}
//
//		@Override
//		public void run() {
//			try {
//				MoveFilterUtil.infoManager();
//			} catch (Exception e) {
//				Log.info("路径检查，用户管理出错" + e);
//			}
//		}
//	}
//
//}
