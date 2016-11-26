package com.app.empire.scene.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 大地图上面非节点解析工具类 <br/>
 * 非节点数据不保存在数据库中<br/>
 * 系统启动时,直接解析文件，加载到内存中<br/>
 */
public class MapParser {

	/**
	 * 解析大地图数据文件
	 * 
	 * @param fileName
	 *            : 大地图数据文件存放目录
	 * @return key: 大地图格子编号, value: 该格子上面所有非节点值(一维数组)
	 * @throws IOException
	 */
	public static Map<String, List<Integer>> parsePhysics(String fileName) throws IOException {
		File file = new File(fileName);
		if (!file.isDirectory()) {
			return null;
		}
		String suffix = "data.titles";
		Map<String, List<Integer>> unNodes = new HashMap<String, List<Integer>>();
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() || !(files[i].getName().endsWith(suffix))) {
				continue;
			}

			String name = files[i].getName();
			String key = name.substring(0, name.length() - suffix.length());
			ArrayList<Integer> temps = new ArrayList<Integer>(500);
			int len = (int) files[i].length();
			byte buff[] = new byte[len];
			FileInputStream fis = new FileInputStream(files[i]);
			fis.read(buff);
			fis.close();
			for (int j = 4; j < len; j++) {
				temps.add((int) buff[++j]);
			}
			unNodes.put(key, temps);
		}
		return unNodes;
	}

	/**
	 * 解析大地图数据文件
	 * 
	 * @param fileName
	 *            : 大地图数据文件存放目录
	 * @return key: 大地图格子编号, value: 该格子上面所有非节点值(一维数组)
	 * @throws IOException
	 */
	public static Map<String, List<Integer>> parsePos(String path, String fileName, String expStartStr) throws IOException {
		fileName = path + "/" + fileName;
		File file = new File(fileName);

		String suffix = "data.titles";
		Map<String, List<Integer>> unNodes = new HashMap<String, List<Integer>>();
		if (file.isDirectory() || !(file.getName().endsWith(suffix))) {
			return null;
		}

		String name = file.getName();
		String key = name.substring(0, name.length() - suffix.length());
		key = key.substring(key.length() - expStartStr.length() + 1, key.length());
		ArrayList<Integer> temps = new ArrayList<Integer>(500);
		int len = (int) file.length();
		byte buff[] = new byte[len];
		FileInputStream fis = new FileInputStream(fileName);
		fis.read(buff);
		fis.close();
		for (int j = 4; j < len; j++) {
			temps.add((int) buff[++j]);
		}
		unNodes.put(key, temps);
		return unNodes;
	}

	public static Map<String, HashMap<String, Integer>> parseRoute(String fileName) throws IOException {
		Map<String, HashMap<String, Integer>> routeMap = new HashMap<String, HashMap<String, Integer>>();
		List<String> lines = FileOperate.readLines(fileName, "");
		for (String line : lines) {
			String[] temps = line.split("\\|");
			String castleKey = temps[0];
			String[] castleValues = temps[1].split(",");
			HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
			int len = castleValues.length;
			if (len >= 3 && len % 3 == 0) {
				for (int i = 0; i < len; i++) {
					String posX = castleValues[i];
					String posY = castleValues[++i];
					int length = Integer.parseInt(castleValues[++i]);
					tempMap.put(posX + "," + posY, length);
				}
			}
			routeMap.put(castleKey, tempMap);
		}
		return routeMap;
	}

	public static List<int[]> initPos(String mapPath, int mapId) throws Exception {
		File file = new File(mapPath);
		List<String> fileList = new ArrayList<String>();
		if (file.isDirectory()) {
			String[] filelist = file.list();
			for (String string : filelist) {
				if (string.startsWith(mapId + "")) {
					fileList.add(string);
				}
			}
		}
		List<int[]> posSet = new ArrayList<int[]>();
		for (String fileName : fileList) {
			Map<String, List<Integer>> unNodesTemps = MapParser.parsePos(mapPath, fileName, mapId + "");
			if (unNodesTemps == null) {
				continue;
			}
			for (Entry<String, List<Integer>> entry : unNodesTemps.entrySet()) {
				String key = entry.getKey();
				List<Integer> temps = entry.getValue();
				String[] str = key.split("_");
				int vx = Integer.parseInt(str[0]) * 1000 / 20;
				int vy = Integer.parseInt(str[1]) * 1000 / 20;
				int i = 0;
				for (int temp : temps) {
					int y = i / 50;
					int x = i - y * 50;
					x = vx + x;
					y = vy + y;
					if (temp == 2) {
						int[] pos = new int[] { x, y };
						posSet.add(pos);
					}
					i++;
				}
			}

		}
		return posSet;
	}
}
