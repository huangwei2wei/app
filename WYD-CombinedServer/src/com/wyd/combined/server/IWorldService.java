package com.wyd.combined.server;

import java.util.List;
import com.wyd.db.service.UniversalManager;

/**
 * The service interface for the TabConsortiaright entity.
 */
public interface IWorldService extends UniversalManager{
    /**
     * 获取分区的所有玩家数量
     * @param areaId
     * @return
     */
    public int getCountByAreaId(int areaId);
    /**
     * 获取分区的所有玩家
     * @param areaId
     * @return
     */
    public List<Object> getPlayerByAreaId(int areaId, int pageIndex);
    /**
     * 更新玩家所属的分区信息
     * @param areaId1 更新分区id
     * @param areaId2 目标分区id
     */
    public void updatePlayerInfo(String s1, String s2);
    /**
     * 删除旧分区的相关信息
     * @param areaId
     */
    public void deleteOldAreaInfo(String areaId);
}