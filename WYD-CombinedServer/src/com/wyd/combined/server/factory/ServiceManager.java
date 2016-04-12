package com.wyd.combined.server.factory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.wyd.combined.server.IAccountService;
import com.wyd.combined.server.IWorldService;
import com.wyd.combined.server.impl.CombinedService;
public class ServiceManager {
    ApplicationContext            context             = new ClassPathXmlApplicationContext("applicationContext.xml");
    private static ServiceManager instance            = null;
    protected Configuration       configuration       = null;
    private CombinedService combinedService;

    private ServiceManager() {
        try {
            this.configuration = new PropertiesConfiguration("config.properties");
            this.combinedService = new CombinedService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ServiceManager getManager() {
        synchronized (ServiceManager.class) {
            if (null == instance) {
                instance = new ServiceManager();
            }
        }
        return instance;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
    
    public IAccountService getAccountService() {
        return (IAccountService) context.getBean("AccountService");
    }
    
    public IWorldService getWorldService() {
        return (IWorldService) context.getBean("WorldService");
    }

    public CombinedService getCombinedService() {
        return combinedService;
    }
}