package com.wyd.combined.server.impl;
import java.util.List;
import org.springframework.context.ApplicationContext;
import com.wyd.combined.dao.IWorldDao;
import com.wyd.combined.server.IWorldService;
import com.wyd.db.service.impl.UniversalManagerImpl;
/**
 * The service class for the TabExtensionUser entity.
 */
public class WorldService extends UniversalManagerImpl implements IWorldService {
    /**
     * The dao instance injected by Spring.
     */
    private IWorldDao      dao;
    /**
     * The service Spring bean id, used in the applicationContext.xml file.
     */
    private static final String SERVICE_BEAN_ID = "WorldService";

    public WorldService() {
        super();
    }

    /**
     * Returns the singleton <code>IExtensionUserService</code> instance.
     */
    public static IWorldService getInstance(ApplicationContext context) {
        return (IWorldService) context.getBean(SERVICE_BEAN_ID);
    }

    /**
     * Called by Spring using the injection rules specified in the Spring beans file "applicationContext.xml".
     */
    public void setDao(IWorldDao dao) {
        super.setDao(dao);
        this.dao = dao;
    }

    public IWorldDao getDao() {
        return this.dao;
    }

    @Override
    public int getCountByAreaId(int areaId) {
        return dao.getCountByAreaId(areaId);
    }

    @Override
    public List<Object> getPlayerByAreaId(int areaId, int pageIndex) {
        return dao.getPlayerByAreaId(areaId, pageIndex);
    }

    /**
     * 更新玩家所属的分区信息
     * @param areaId1 更新分区id
     * @param areaId2 目标分区id
     */
    public void updatePlayerInfo(String s1, String s2){
        dao.updatePlayerInfo(s1, s2);
    }
    
    /**
     * 删除旧分区的相关信息
     * @param areaId
     */
    public void deleteOldAreaInfo(String areaId){
        dao.deleteOldAreaInfo(areaId);
    }
}