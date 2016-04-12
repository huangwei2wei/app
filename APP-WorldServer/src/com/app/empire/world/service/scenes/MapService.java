package com.app.empire.world.service.scenes;

import java.util.HashMap;
import java.util.List;

import com.app.empire.world.model.map.MapUnitVo;
import com.app.empire.world.model.map.ScenesMap;
import com.app.empire.world.service.factory.ServiceManager;

/*
 * 场景地图服务
 * 1、怪物行走控制，player移动更新及位置同步 
 * 2、怪物AI策略 
 * 3、区域性广播，场景广播 
 * 4、战斗逻辑 
 * 5、AOI服务（Area Of Interest ） 
 * 6、碰撞检测 
 * 7、自动寻径
 * **需要地图信息 怪物信息
 */

public class MapService {
	/**
	 * 寻路
	 * 
	 * @param startX 起始x
	 * @param startY 起始y
	 * @param endX 目标x
	 * @param endY 目标y
	 * @param mapId 题图id
	 * @return
	 */
	public List<String> searchRoad(int startX, int startY, int endX, int endY, int mapId) {
		String startkey = getGridKey(mapId, startX, startY); // 起始位置对应的方格
		String endKey = getGridKey(mapId, endX, endY);// 目标位置对应的方格
		ScenesMap scenesMap = ServiceManager.getManager().getMapInfoService().getMapInfo(mapId);
		HashMap<String, MapUnitVo> mapUnit = scenesMap.getMapUnit();// 地图方格数据

		return null;
	}

	/**
	 * 通过坐标获取格子key(支持负坐标)
	 * 
	 * @param mapId 地图id
	 * @param x
	 * @param y
	 * @return String
	 */
	public String getGridKey(int mapId, int x, int y) {
		ScenesMap scenesMap = ServiceManager.getManager().getMapInfoService().getMapInfo(mapId);
		int splitWidth = scenesMap.getSplitWidth();// 单元格的宽
		int splitHigh = scenesMap.getSplitHigh();// 单元格的高

		int widthNum = 0;
		int highNum = 0;
		if (x > 0)
			widthNum = (int) Math.ceil((double) x / splitWidth);// 玩家宽度格子数
		else
			widthNum = (int) -Math.ceil(Math.abs((double) x / splitWidth));

		if (y > 0)
			highNum = (int) Math.ceil((double) y / splitHigh);// 玩家高度格子数
		else
			highNum = (int) -Math.ceil(Math.abs((double) y / splitHigh));

		// System.out.println(widthNum + ":" + highNum);
		return widthNum + ":" + highNum;
	}

}
