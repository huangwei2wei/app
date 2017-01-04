package com.app.empire.world.service.map.manager;

public interface EnterMapResult {

	public static final int SUCCESS = 1; // 进入成功
	public static final int CLEAR = 2; // 地图已被清理
	public static final int CAMPAIGN_ERROR = 3; // 副本错误
	public static final int CAMPAIGN_DESTORY = 4; // 副本不存在
	public static final int TEMP_ERROR = 5; // 模型不存在
}
