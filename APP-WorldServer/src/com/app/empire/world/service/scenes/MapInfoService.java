package com.app.empire.world.service.scenes;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.app.empire.world.model.map.ScenesMap;

/**
 * 地图基础数据处理
 * 
 * @author doter
 * 
 */

@Service
public class MapInfoService {
	private HashMap<Integer, ScenesMap> scenesMap = new HashMap<Integer, ScenesMap>();// 场景地图数据，地图id->地图场景数据

	/**
	 * 加载地图配置并初始化数据
	 */
	public void loadMap() {

	}

	/**
	 * 获取地图数据
	 * 
	 * @param mapId 地图id
	 * @return
	 */
	public ScenesMap getMapInfo(Integer mapId) {
		return scenesMap.get(mapId);
	}

}
