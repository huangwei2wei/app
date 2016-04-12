package com.app.empire.gameaccount.service.factory;
import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.gameaccount.service.impl.AccountService;
import com.app.empire.gameaccount.stub.WorldStub;
import com.app.session.SessionRegistry;

@Service
public class ServiceFactory {
	@Autowired
	private AccountService accountService;
	private Configuration configuration;
	private WorldStub worldStub;
	private ClientListManager clientListManager;
	private SessionRegistry registry;
	private static ServiceFactory serviceFactory;

	protected ServiceFactory() {
		try {
			this.configuration = new PropertiesConfiguration("config.properties");
			this.clientListManager = new ClientListManager(new File(Thread.currentThread().getContextClassLoader()
					.getResource("clients.txt").getPath()));
			this.registry = new SessionRegistry();
			this.clientListManager.start();
			
			this.worldStub = new WorldStub(this.configuration, this.registry);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public WorldStub getWorldStub() {
		return worldStub;
	}

	public ClientListManager getClientListManager() {
		return clientListManager;
	}

	public static ServiceFactory getServiceFactory() {
		return serviceFactory;
	}

	public void setServiceFactory(ServiceFactory serviceFactory) {
		ServiceFactory.serviceFactory = serviceFactory;
	}
 
	
	
}
