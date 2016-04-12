package com.wyd.combined.dao;
import java.util.List;
import com.wyd.combined.bean.Empireaccount;
import com.wyd.db.dao.UniversalDao;
/**
 * The DAO interface for the TabConsortiaright entity.
 */
public interface IAccountDao extends UniversalDao {
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