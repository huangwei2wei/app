package com.wyd.combined.dao.impl;
import java.util.List;
import com.wyd.combined.dao.IWorldDao;
import com.wyd.db.dao.impl.UniversalDaoHibernate;
import com.wyd.db.page.PageList;
/**
 * The DAO class for the TabConsortiaright entity.
 */
public class WorldDao extends UniversalDaoHibernate implements IWorldDao {
    public WorldDao() {
        super();
    }
    
    /**
     * 获取分区的所有玩家数量
     * @param areaId
     * @return
     */
    public int getCountByAreaId(int areaId){
        Object obj = getClassObj("select count(*) from Player where areaId=?", new Object[]{areaId});
        return Integer.parseInt(obj.toString());
    }
    
    /**
     * 获取分区的所有玩家
     * @param areaId
     * @return
     */
    public List<Object> getPlayerByAreaId(int areaId, int pageIndex){
        String hsql = "from Player where areaId=?";
        String countHql = "select count(*)" + hsql;
        PageList pl = getPageList(hsql, countHql, new Object[]{areaId}, pageIndex, 1000);
        return pl.getList();
    }
    
    /**
     * 更新玩家所属的分区信息
     * @param areaId1 更新分区id
     * @param areaId2 目标分区id
     */
    public void updatePlayerInfo(String s1, String s2){
        int areaId1 = Integer.parseInt(s1.split("_")[1]);
        int areaId2 = Integer.parseInt(s2.split("_")[1]);
        this.executeSql("update tab_player set areaId=? where areaId=?", new Object[]{areaId2, areaId1});
        //更新玩家登录记录
        this.executeSql("update log_playeronline set areaId=? where areaId=?", new Object[]{areaId2, areaId1});
        //更新玩家排行榜
        this.executeSql("update log_playerstaweek set area_id=? where area_id=?", new Object[]{s2, s1});
        //更新玩家每日奖励
        this.executeSql("update tab_rewardrecord set area_id=? where area_id=?", new Object[]{s2, s1});
        //更新玩家活动奖励
        this.executeSql("update log_activities_award set area_id=? where area_id=?", new Object[]{s2, s1});
        //更新玩家登录奖励
        this.executeSql("update tab_login_reward set area_id=? where area_id=?", new Object[]{s2, s1});
        //更新玩家签到记录
        this.executeSql("update tab_sign set area_id=? where area_id=?", new Object[]{s2, s1});
        //更新玩家签到记录
        this.executeSql("update log_activities_award set area_id=? and is_send='Y' where area_id=?", new Object[]{s2, s1});
    }
    
    /**
     * 删除旧分区的相关信息
     * @param areaId
     */
    public void deleteOldAreaInfo(String areaId){
        this.executeSql("DELETE FROM tab_activities_award WHERE area_id=?", new Object[]{areaId});
        this.executeSql("DELETE FROM tab_bulletin WHERE areaId=?", new Object[]{areaId});
        this.executeSql("DELETE FROM tab_daily_activities WHERE areaId=?", new Object[]{areaId});
        this.executeSql("DELETE FROM tab_full_service_reward WHERE area_id=?", new Object[]{areaId});
        this.executeSql("DELETE FROM tab_invite_reward WHERE area_id=?", new Object[]{areaId});
        this.executeSql("DELETE FROM tab_service_info WHERE area_id=?", new Object[]{areaId});
        this.executeSql("DELETE FROM tab_login_reward WHERE area_id=?", new Object[]{areaId});
        this.executeSql("DELETE FROM tab_magnification WHERE area_id=?", new Object[]{areaId});
        this.executeSql("DELETE FROM tab_operationconfig WHERE areaId=?", new Object[]{areaId});
    }
}