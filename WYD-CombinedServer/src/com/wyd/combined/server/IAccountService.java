package com.wyd.combined.server;

import java.util.List;
import com.wyd.combined.bean.Empireaccount;
import com.wyd.db.service.UniversalManager;

/**
 * The service interface for the TabConsortiaright entity.
 */
public interface IAccountService extends UniversalManager{
    /**
     * 获取游戏分区列表
     * @return
     */
    public List<String> getAreaList();
    /**
     * 获取指定分区信息
     * @param accountId
     * @param serviceId
     * @return
     */
    public Empireaccount getEmpireaccount(int accountId, String serviceId);
    /**
     * 删除合并后多余的分区信息
     * @param serviceId
     */
    public void deleteEmpireaccount(String serviceId);
}