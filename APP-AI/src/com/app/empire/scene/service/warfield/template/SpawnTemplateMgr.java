package com.app.empire.scene.service.warfield.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.constant.SpwanInfoType;
import com.chuangyou.xianni.entity.spawn.SpawnInfo;
import com.chuangyou.xianni.sql.dao.DBManager;

public class SpawnTemplateMgr {

	// 刷新节点配置
	private static Map<Integer, Map<Integer, SpawnInfo>>	spwnInfo				= new HashMap<Integer, Map<Integer, SpawnInfo>>();
	// ID标记与节点ID关系表
	private static Map<Integer, Integer>					tagId_spanId_mapping	= new HashMap<>();
	private static Map<Integer, List<SpawnInfo>>			reLiveNodes				= new HashMap<>();

	public static boolean init() {
		return reloadSpawnInfoTemp();
	}

	public static Map<Integer, SpawnInfo> getFieldSpawnInfos(int mapKey) {
		Map<Integer, SpawnInfo> result =  spwnInfo.get(mapKey);
		if(result == null){
			result = new HashMap<>();
		}
		return result;
	}

	public static int getSpwanId(int tagId) {
		if (tagId_spanId_mapping.containsKey(tagId)) {
			return tagId_spanId_mapping.get(tagId);
		} else {
			return 0;
		}
	}

	public static boolean reloadSpawnInfoTemp() {
		Map<Integer, SpawnInfo> spawns = DBManager.getSpawnInfoDao().getAll();
		for (Integer key : spawns.keySet()) {
			SpawnInfo spawn = spawns.get(key);

			// 全部节点
			if (!spwnInfo.containsKey(spawn.getMapid())) {
				spwnInfo.put(spawn.getMapid(), new HashMap<Integer, SpawnInfo>());
			}
			spwnInfo.get(spawn.getMapid()).put(spawn.getId(), spawn);
			tagId_spanId_mapping.put(spawn.getTagId(), spawn.getId());

			// 地图存放点
			if (spawn.getEntityType() == SpwanInfoType.REVIVAL_NODE) {
				List<SpawnInfo> list = reLiveNodes.get(spawn.getMapid());
				if (list == null) {
					list = new ArrayList<>();
					reLiveNodes.put(spawn.getMapid(), list);
				}
				list.add(spawn);
			}

		}
		return true;
	}

	/** 寻找复活点 */
	public static Vector3 getRevivalNode(int mapId, Vector3 v3) {
		List<SpawnInfo> nodes = reLiveNodes.get(mapId);
		if (nodes == null) {
			return null;
		}

		if (nodes.size() == 1) {
			return nodes.get(0).getPosition();
		}

		SpawnInfo finder = null;
		int distance = 0;
		for (SpawnInfo info : nodes) {
			int nids = getDistance(info.getPosition(), v3);
			if (finder == null) {
				finder = info;
				distance = nids;
				continue;
			}
			if (nids < distance) {
				finder = info;
			}
		}
		return finder.getPosition();
	}

	private static int getDistance(Vector3 v1, Vector3 v2) {
		return (int) Vector3.distance(new Vector3(v1.x, 0, v1.z), new Vector3(v2.x, 0, v2.z));
	}
}
