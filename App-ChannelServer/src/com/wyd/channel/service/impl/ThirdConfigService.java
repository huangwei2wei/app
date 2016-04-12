package com.wyd.channel.service.impl;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.app.db.service.impl.UniversalManagerImpl;
import com.wyd.channel.bean.ThirdConfig;
import com.wyd.channel.dao.IThirdConfigDao;
import com.wyd.channel.service.IThirdConfigService;

/**
 * The service class for the TabConsortiaright entity.
 * @author zengxc
 */
public class ThirdConfigService extends UniversalManagerImpl implements IThirdConfigService {
	/**
	 * The dao instance injected by Spring.
	 */
	private IThirdConfigDao dao;
	/**
	 * The service Spring bean id, used in the applicationContext.xml file.
	 */
	private static final String SERVICE_BEAN_ID = "ThirdConfigService";
	
	public ThirdConfigService() {
		super();
	}
	/**
	 * Returns the singleton <code>IConsortiarightService</code> instance.
	 */
	public static ThirdConfigService getInstance(ApplicationContext context) {
		return (ThirdConfigService)context.getBean(SERVICE_BEAN_ID);
	}
	/**
	 * Called by Spring using the injection rules specified in 
	 * the Spring beans file "applicationContext.xml".
	 */
	public void setDao(IThirdConfigDao dao) {
        super.setDao(dao);
        this.dao = dao;
	}
	public IThirdConfigDao getDao() {
		return this.dao;
	}
	    @SuppressWarnings("unchecked")
	    public ThirdConfig getThirdConfig(int appId, String channelId) {
	        String hql="from ThirdConfig where appId=? and channelId=?";
	        List<ThirdConfig> thirdConfigList = this.getList(hql, new Object[]{appId,channelId});
	        if (thirdConfigList != null && !thirdConfigList.isEmpty()) {
	            return thirdConfigList.get(0);
	        } else {
	            return null;
	        }
	    }
	    
	    /**
	     * 根据第三方渠道应用ID和渠道ID获取第三方充值配置信息
	     * @param appId         第三方渠道应用ID     
	     * @param channelId     渠道ID
	     * @return
	     */
	    @SuppressWarnings("unchecked")
	    public ThirdConfig getThirdConfigByGameId(String gameId, String channelId) {
	        String hql="from ThirdConfig where gameId=? and channelId=?";
	        List<ThirdConfig> thirdConfigList = this.getList(hql, new Object[]{gameId,channelId});
	        if (thirdConfigList != null && !thirdConfigList.isEmpty()) {
	            return thirdConfigList.get(0);
	        } else {
	            return null;
	        }
	    }
   
}