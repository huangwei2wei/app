package com.chuangyou.xianni.drop.templete;

import java.util.HashMap;
import java.util.Map;

import com.chuangyou.xianni.entity.drop.DropInfo;
import com.chuangyou.xianni.entity.drop.DropItemInfo;
import com.chuangyou.xianni.sql.dao.DBManager;

public class DropTempleteMgr {

	public static Map<Integer, DropInfo> dropPool = new HashMap<>();
	
	public static Map<Integer, Map<Integer, DropItemInfo>> dropItemMap = new HashMap<>();
	
	public static boolean init(){
		return reloadTemplete();
	}
	
	public static boolean reloadTemplete(){
		dropPool = DBManager.getDropConfigDao().getDropInfo();
		dropItemMap = DBManager.getDropConfigDao().getDropItem();
		return true;
	}

	public static Map<Integer, DropInfo> getDropPool() {
		return dropPool;
	}

	public static Map<Integer, Map<Integer, DropItemInfo>> getDropItemMap() {
		return dropItemMap;
	}
}
