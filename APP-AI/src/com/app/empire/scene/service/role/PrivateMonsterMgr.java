package com.app.empire.scene.service.role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chuangyou.xianni.role.objects.PrivateMonster;

/** 私人怪物管理器：管理玩家自己刷新出来的怪物 */
public class PrivateMonsterMgr {
	private static Map<Long, Map<Long, PrivateMonster>>	privateMonsters	= new ConcurrentHashMap<>();
	private static final int							EXPIRED_TIME	= 60 * 60 * 1000;			// 怪物定时清理

	/** 添加一个自有怪 */
	public static boolean add(PrivateMonster pmonster) {
		Map<Long, PrivateMonster> ownerC = privateMonsters.get(pmonster.getCreater());
		if (ownerC == null) {
			ownerC = new HashMap<>();
		}
		ownerC.put(pmonster.getId(), pmonster);
		privateMonsters.put(pmonster.getCreater(), ownerC);
		return true;
	}

	/** 删除 */
	public static boolean remove(PrivateMonster pmonster) {
		Map<Long, PrivateMonster> ownerC = privateMonsters.get(pmonster.getCreater());
		if (ownerC == null) {
			return true;
		}
		synchronized (ownerC) {
			ownerC.remove(pmonster);
		}
		
		if (pmonster.getField() != null) {
			pmonster.getField().addDeathLiving(pmonster);
		}
		return true;
	}

	/** 获取某个玩家的自有怪 */
	public static List<PrivateMonster> get(long playerId) {
		Map<Long, PrivateMonster> ownerC = privateMonsters.get(playerId);
		List<PrivateMonster> result = new ArrayList<>();
		if (ownerC != null) {
			synchronized (ownerC) {
				result.addAll(ownerC.values());
			}
		}
		return result;
	}

	/** 定时清理怪物 */
	public static void clearExpired() {
		List<PrivateMonster> all = new ArrayList<>();
		synchronized (privateMonsters) {
			for (Map<Long, PrivateMonster> maps : privateMonsters.values()) {
				all.addAll(maps.values());
			}
		}
		for (PrivateMonster pm : all) {
			if (pm.expired()) {
				pm.destory();
				if (pm.getField() != null) {
					pm.getField().leaveField(pm);
				}
			}
		}

	}
}
