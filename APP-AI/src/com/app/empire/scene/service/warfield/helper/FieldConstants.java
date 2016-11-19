package com.app.empire.scene.service.warfield.helper;

public class FieldConstants {
	/**
     * 刷怪定时类型
     * 
     * @author wanyi
     *        
     */
    public interface MonsterReflushTimerType
    {
    	int none = 1; //0:根据开服刷新
    	
        int timing = 2;// 1:每天定时刷
        
        int utcRange = 3;// 2：utc时间范围内刷
    }
    
    /**
     * 刷新重置类型
     * @author wanyi
     *
     */
    public interface MonsterRestType
    {
        int normal = 1;// 普通模式
        int boss = 2; // boss模式
        int dunguon = 3;// 副本模式
    }
    
}
