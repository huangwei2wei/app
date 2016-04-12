package com.app.dispatch;

import java.io.File;

import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ConfigMenger implements Runnable {
	private Configuration configuration;
	private File file;
	private long lastModified;
	private String fileName;
	public ConfigMenger(String fileName) throws ConfigurationException {
		this.fileName = fileName;
		ClassLoader a =  Thread.currentThread().getContextClassLoader();
		String filePath = Thread.currentThread().getContextClassLoader().getResource(fileName).getPath();
		file = new File(filePath);
		this.lastModified = file.lastModified();
		loadSources();
		start();
	}

	private void loadSources() throws ConfigurationException {
		// 加载配置文件
		this.configuration = new PropertiesConfiguration(fileName);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	protected boolean isFileModified() {
		long t = this.file.lastModified();
		if (t != this.lastModified) {
			this.lastModified = t;
			return true;
		}
		return false;
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setName("ConfigMenger-Thread");
		thread.start();
	}

	public void run() {
		while (true) {
			if (isFileModified())
				try {
					loadSources();
				} catch (Exception e) {
					e.printStackTrace();
				}
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
