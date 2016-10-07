package com.app.empire.scene.service.role.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chuangyou.xianni.entity.spawn.NpcInfo;
import com.chuangyou.xianni.sql.dao.DBManager;
import com.chuangyou.xianni.sql.dao.NpcInfoDao;

public class NpcInfoTemplateMgr {

	/// npc
	public static Map<Integer, NpcInfo> npcInfoTemps = new HashMap<Integer, NpcInfo>();

	/// 转场点等踩点触发的点
	// public static Map<Integer, NpcInfo> touchPointTemps = new
	/// HashMap<Integer, NpcInfo>();

	public static boolean init() {
		return reloadNpcInfoTemp();
	}

	public static boolean reloadNpcInfoTemp() {
		NpcInfoDao dao = DBManager.getNpcInfoDao();
		List<NpcInfo> infos = dao.getAll();
		for (NpcInfo npc : infos) {
			npcInfoTemps.put(npc.getNpcId(), npc);
			// if (npc.getType() == 2) // npc
			// {
			// npcInfoTemps.put(npc.getNpcId(), npc);
			// } else if (npc.getType() == 3) // 转场点
			// {
			// touchPointTemps.put(npc.getNpcId(), npc);
			// }
		}
		return true;
	}

	public static NpcInfo getNpcInfo(int npcId) {
		return npcInfoTemps.get(npcId);
	}
}
