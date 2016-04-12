package com.wyd.combined.dao.impl;
import java.util.List;
import com.wyd.combined.bean.Empireaccount;
import com.wyd.combined.dao.IAccountDao;
import com.wyd.db.dao.impl.UniversalDaoHibernate;
/**
 * The DAO class for the TabConsortiaright entity.
 */
public class AccountDao extends UniversalDaoHibernate implements IAccountDao {
    public AccountDao() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getAreaList() {
        return this.getListBySql("SELECT serverid FROM tab_empireaccount GROUP BY serverid ORDER BY LENGTH(serverid),serverid", new Object[]{});
    }
    
    public Empireaccount getEmpireaccount(int accountId, String serviceId){
        return (Empireaccount)this.getClassObj("from Empireaccount where accountId=? and serverid=?", new Object[]{accountId, serviceId});
    }
    
    public void deleteEmpireaccount(String serviceId){
        this.executeSql("delete from tab_empireaccount where serverid=?", new Object[]{serviceId});
    }
}