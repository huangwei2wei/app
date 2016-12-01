package com.app.empire.scene.service;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.scene.service.base.impl.GameConfigService;
import com.app.empire.scene.service.warField.FieldMgr;


@Service
public class ServiceManager {
	private static ServiceManager serviceManager;
	private PropertiesConfiguration configuration;

	@Autowired
	private GameConfigService gameConfigService;
	
//	private static ServiceManager serviceManager = new ServiceManager();
//	private ServiceManager() {
//	}
//	@Autowired
//	public static ServiceManager getManager() {
//		return serviceManager;
//	}
	public static void setServiceManager(ServiceManager serviceManager) {
		ServiceManager.serviceManager = serviceManager;
	}
	public static ServiceManager getManager() {
		return serviceManager;
	}
	public void initService() {
		try {
			this.loadConfig();
			getGameConfigService().load();
			FieldMgr.getIns().initilize();
			
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void loadConfig() throws ConfigurationException {
		this.configuration = new PropertiesConfiguration("config.properties");
	}
	public PropertiesConfiguration getConfiguration() {
		return this.configuration;
	}

	public GameConfigService getGameConfigService() {
		return gameConfigService;
	}

 
}
