package com.wyd.channel.service.factory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wyd.channel.service.IThirdConfigService;
import com.wyd.channel.utils.ThreadPool;


public class ServiceManager {
    ApplicationContext            context       = new ClassPathXmlApplicationContext("applicationContext.xml");
    private static ServiceManager instance      = null;
    private Configuration         configuration = null;
    private ThreadPool              httpThreadPool;
    private ServiceManager() {
        try {
            configuration = new PropertiesConfiguration("config.properties");
            // 包含http任务的线程池
            httpThreadPool = new ThreadPool(20);
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
    
    public void init() {
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
    public ThreadPool getHttpThreadPool() {
        return httpThreadPool;
    }

    /** 第三方渠道配置表 by: zengxc
    * @return
    */
   public IThirdConfigService getThirdConfigService() {
       return (IThirdConfigService) context.getBean("ThirdConfigService");
   }

   
}