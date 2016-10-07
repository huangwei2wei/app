package com.app.empire.scene.service.role.objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.chuangyou.xianni.role.helper.RoleConstants.RoleType;

/**
 * 采集物
 * 
 * @author laofan
 * 
 */
public class Gather extends Living {

	private String			name;

	/**
	 * 采集物采集CD缓存
	 */
	private Map<Long, Long>	playerCdTimers	= new HashMap<>();

	public Gather(long id, String name) {
		super(id);
		setType(RoleType.gather);
		this.name = name;
	}

	@Override
	public String toString() {
		return "Gather [name=" + name + ", getId()=" + getId() + "]";
	}

	/**
	 * 添加CD
	 * 
	 * @param playerId
	 * @param time
	 */
	public void addCdTime(long playerId, long time) {
		synchronized (playerCdTimers) {
			if (playerCdTimers.size() > 10) {
				clearCd();
			}
			playerCdTimers.put(playerId, time);
		}
	}

	/**
	 * 删除CD时间
	 * 
	 * @param playerId
	 */
	public void removeCdTime(long playerId) {
		synchronized (playerCdTimers) {
			playerCdTimers.remove(playerId);
		}
	}

	/**
	 * 获取记录CD的时间
	 * 
	 * @param playerId
	 * @return
	 */
	public long getTime(long playerId) {
		if (playerCdTimers.containsKey(playerId)) {
			return playerCdTimers.get(playerId);
		}
		return 0;
	}

	@Override
	public boolean onDie(Living source) {
		if (super.onDie(source)) {
			this.playerCdTimers.clear();
		}
		return true;
	}

	/**
	 * 清理一下有些时间已经很久的CD
	 */
	public void clearCd() {
		synchronized (playerCdTimers) {
			Iterator<Entry<Long, Long>> it = playerCdTimers.entrySet().iterator();
			long currentT = System.currentTimeMillis();
			while (it.hasNext()) {
				if (currentT - it.next().getValue() > 60 * 1000) {
					it.remove();
				}
			}
		}
	}

}
