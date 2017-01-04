package com.app.empire.scene.service.warField.spawn;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.FieldInfo;
import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.util.StringUtils;
import com.app.empire.scene.util.TimeUtil;

/** 管理且只管理大地图时间范围内刷新的刷怪点 */
public class TimeControlerNodeMgr {
	protected static Logger log = Logger.getLogger(TimeControlerNodeMgr.class);

	public static Set<SpwanNode> TM_NODES = new HashSet<>();

	public static void addNode(SpwanNode node) {
		FieldSpawn temp = node.getSpawnInfo();
		if (temp.getTimerType() == 0) {
			return;
		}
		if (StringUtils.isNullOrEmpty(temp.getTimerBegin()) && StringUtils.isNullOrEmpty(temp.getTimerEnd())) {
			return;
		}
		FieldInfo map = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().get(temp.getMapid());
		if (map.getType() != 1) {
			return;
		}
		TM_NODES.add(node);
	}

	public static void check() {
		for (SpwanNode node : TM_NODES) {
			try {
				begin(node);
				end(node);
			} catch (Exception e) {
				log.error("TimeControlerNodeMgr check error:" + node.getSpwanId(), e);
			}
		}
	}

	private static void begin(SpwanNode node) {
		// 今天已经刷新过(5分钟刷新间隔)
		if (!isOpen(node.getSpawnInfo().getTimerBegin(), node.getSpawnInfo().getTimerEnd()) || node.getState().getCode() == NodeState.WORK) {
			return;
		}
		try {
			if (node.getTimerControlerTime() >= TimeUtil.getDate(node.getSpawnInfo().getTimerBegin()).getTime()) {
				return;
			}
		} catch (Exception e) {
			log.error(node.getSpawnInfo().getId() + "-----" + node.getSpawnInfo().getTimerBegin());
		}
		// 刷新
		node.revive();
		node.setTimerControlerTime(System.currentTimeMillis());
	}

	private static void end(SpwanNode node) {
		// 今天已经刷新过(5分钟刷新间隔)
		if (isOpen(node.getSpawnInfo().getTimerBegin(), node.getSpawnInfo().getTimerEnd()) || node.getState().getCode() == NodeState.OVER) {
			return;
		}
		// 结束
		node.forceStop();
	}

	/** 前后五分钟之内 */
	private static boolean isOpen(String beginTime, String endTime) {
		try {
			Date begin = TimeUtil.getDate(beginTime);
			Date end = TimeUtil.getDate(endTime);
			long now = System.currentTimeMillis();
			if (now >= begin.getTime() && now <= end.getTime()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			log.error("difference" + beginTime);
		}
		return false;
	}
}
