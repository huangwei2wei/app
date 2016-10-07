package com.app.empire.scene.service.role.template;

import java.util.HashMap;
import java.util.Map;

import com.chuangyou.xianni.entity.spawn.MonsterInfo;
import com.chuangyou.xianni.sql.dao.DBManager;

public class MonsterInfoTemplateMgr {
	public static Map<Integer, MonsterInfo> monsterInfoTemps = new HashMap<Integer, MonsterInfo>();

	public static boolean init()
	{
		return reloadMonsterInfoTemp();
	}
	
	public static boolean reloadMonsterInfoTemp()
	{
		monsterInfoTemps = DBManager.getMonsterInfoDao().getAll();
		if(monsterInfoTemps == null)
			return false;
		return true;
	}
	
	public static MonsterInfo get(int tempId){
		return monsterInfoTemps.get(tempId);
	}
}
