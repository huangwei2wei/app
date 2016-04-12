package com.app.empire.world.service.scenes;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import com.app.empire.world.model.player.WorldPlayer;

/**
 * 地图 aoi 模块
 * 
 * 维护可视范围
 * 
 * 场景模块通知它改变对象的位置信息。AOI 服务则发送 AOI 消息给场景。
 * 
 * @author doter
 * 
 */

public class AoiService {
	/*** map　房间id/地图id ->九宫格块id -> WorldPlayer ***/
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Vector<WorldPlayer>>> aoiMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, Vector<WorldPlayer>>>();
	
}
