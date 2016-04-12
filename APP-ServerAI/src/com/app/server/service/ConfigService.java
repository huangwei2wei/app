package com.app.server.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.app.server.bean.ServerConfigBean;
/**
 * 管理国家地区配置，新玩家分配机率配置
 * 
 * @author Administrator
 */
public class ConfigService implements Runnable {

	private PropertiesConfiguration rnuConfig;
	private Map<String, Map<String, Map<Integer, ServerInfo>>> serverListMap = new ConcurrentHashMap<String, Map<String, Map<Integer, ServerInfo>>>();
	private File randomFile;
	private long randomLastModified;
	private PropertiesConfiguration versionConfig;
	private File versionFile;
	private long versionLastModified;
	private File configFile;
	private long configLastModified;
	private List<String> configList;
	private File rnuFile;
	private long rnuLastModified;

	public ConfigService() throws ConfigurationException {
		try {
			randomFile = new File(Thread.currentThread().getContextClassLoader().getResource("serviceinfo.xml").getPath());
			this.randomLastModified = randomFile.lastModified();
			loadSources();

			versionConfig = new PropertiesConfiguration("version.properties");
			versionFile = new File(Thread.currentThread().getContextClassLoader().getResource("version.properties").getPath());
			this.versionLastModified = versionFile.lastModified();

			configFile = new File(Thread.currentThread().getContextClassLoader().getResource("config.txt").getPath());
			this.configLastModified = configFile.lastModified();

			rnuConfig = new PropertiesConfiguration("reviewandupdate.properties");
			rnuFile = new File(Thread.currentThread().getContextClassLoader().getResource("reviewandupdate.properties").getPath());
			this.rnuLastModified = rnuFile.lastModified();

			loadConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setName("ConfigService-Thread");
		thread.start();
	}

	public void run() {
		while (true) {
			try {
				long t = this.randomFile.lastModified();
				if (t != this.randomLastModified) {
					this.randomLastModified = t;
					loadSources();
				}

				t = this.versionFile.lastModified();
				if (t != this.versionLastModified) {
					this.versionLastModified = t;
					versionConfig = new PropertiesConfiguration("version.properties");
				}

				t = this.configFile.lastModified();
				if (t != this.configLastModified) {
					this.configLastModified = t;
					loadConfig();
				}

				t = this.rnuFile.lastModified();
				if (t != this.rnuLastModified) {
					this.rnuLastModified = t;
					rnuConfig = new PropertiesConfiguration("reviewandupdate.properties");
				}

				Thread.sleep(10000L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void loadSources() throws ConfigurationException {
		// 加载服务器列表配置
		// serverListMap.clear();
		String fileName = Thread.currentThread().getContextClassLoader().getResource("serviceinfo.xml").getPath();
		File inputXml = new File(fileName);
		try {
			SAXReader saxReader = new SAXReader();
			saxReader.setEncoding("UTF-8");
			Document document = saxReader.read(inputXml);
			Element employees = document.getRootElement();
			for (Iterator areaIterator = employees.elementIterator(); areaIterator.hasNext();) {// 区
				Element areaEmployee = (Element) areaIterator.next();
				String areaName = areaEmployee.attributeValue("id");
				Map<String, Map<Integer, ServerInfo>> groupMap;
				if (!serverListMap.containsKey(areaName)) {// 区不存在
					groupMap = new ConcurrentHashMap<String, Map<Integer, ServerInfo>>();
					serverListMap.put(areaName, groupMap);
				} else {
					groupMap = serverListMap.get(areaName);
				}

				for (Iterator groupIterator = areaEmployee.elementIterator(); groupIterator.hasNext();) {// 组
					Element groupEmployee = (Element) groupIterator.next();
					String groupName = groupEmployee.attributeValue("id");
					Map<Integer, ServerInfo> serverList;
					if (!groupMap.containsKey(groupName)) {// 组不存在
						serverList = new ConcurrentHashMap<Integer, ServerInfo>();
						groupMap.put(groupName, serverList);
					} else {
						serverList = groupMap.get(groupName);
					}

					for (Iterator machineIterator = groupEmployee.elementIterator(); machineIterator.hasNext();) {// 机器列表
						Element machineEmployee = (Element) machineIterator.next();
						int machineId = Integer.parseInt(machineEmployee.attributeValue("id"));
						ServerConfigBean serverConfigBean;
						ServerInfo serverInfo;
						if (!serverList.containsKey(machineId)) {
							serverConfigBean = new ServerConfigBean();
							serverInfo = new ServerInfo();
							serverList.put(machineId, serverInfo);
						} else {
							serverInfo = serverList.get(machineId);
							serverConfigBean = serverInfo.getConfig();
						}

						serverConfigBean.setServerId(machineId);
						serverConfigBean.setArea(areaEmployee.attributeValue("id"));
						serverConfigBean.setGroup(groupEmployee.attributeValue("id"));
						serverConfigBean.setName(machineEmployee.attributeValue("name"));
						serverConfigBean.setRandom(Integer.parseInt(machineEmployee.getText()));
						serverConfigBean.setIstest(Integer.parseInt(machineEmployee.attributeValue("istest")));
						serverConfigBean.setOpenudid(Integer.parseInt(machineEmployee.attributeValue("openudid")));
						serverConfigBean.setBulletin(machineEmployee.attributeValue("bulletin"));
						serverConfigBean.setOrder(Integer.parseInt(machineEmployee.attributeValue("order")));
						serverInfo.setConfig(serverConfigBean);
					}
				}
			}
			document = null;
			inputXml = null;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private void loadConfig() throws IOException {
		List<String> configList = new ArrayList<String>();
		FileReader read = null;
		BufferedReader br = null;
		try {
			read = new FileReader(Thread.currentThread().getContextClassLoader().getResource("config.txt").getPath());
			br = new BufferedReader(read);
			String row;
			while ((row = br.readLine()) != null) {
				if (!row.startsWith("#") && row.length() > 0) {
					configList.add(row);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				br.close();
			}
			if (null != read) {
				read.close();
			}
		}
		this.configList = configList;
	}

	/**
	 * 判断某个地区是否有配置
	 */
	public boolean exisArea(String area) {
		return serverListMap.containsKey(area);
	}

	/**
	 * 判断某个地区是否有配置
	 */
	public boolean exisGroup(String area, String group) {
		if (exisArea(area)) {
			if (serverListMap.get(area).containsKey(group))
				return true;
		}
		return false;
	}

	/**
	 * 判断某个地区的某个服务器是否有配置
	 * 
	 * @param area
	 * @param machine
	 * @return
	 */
	public boolean exisMachine(String area, String group, Integer serverid) {
		Map<String, Map<Integer, ServerInfo>> groupMap = serverListMap.get(area);
		if (null != groupMap) {
			Map<Integer, ServerInfo> randomMap = groupMap.get(group);
			if (null != randomMap) {
				return randomMap.containsKey(serverid);
			}
		}
		return false;
	}

	/**
	 * 获取所有地区
	 */
	public List<String> getAllArea() {
		return new ArrayList<String>(serverListMap.keySet());
	}

	/**
	 * 获取所有组
	 */
	public List<String> getAllGroup(String area) {
		return new ArrayList<String>(serverListMap.get(area).keySet());
	}

	/**
	 * 获取指定分区下的所有服务器
	 * 
	 * @param area
	 * @return
	 */
	public List<Integer> getMachine(String area, String group) {
		Map<String, Map<Integer, ServerInfo>> groupMap = serverListMap.get(area);
		if (null != groupMap) {
			Map<Integer, ServerInfo> machineMap = groupMap.get(group);
			if (null != machineMap) {
				return new ArrayList<Integer>(machineMap.keySet());
			}
		}
		return new ArrayList<Integer>();
	}

	/**
	 * 获取指定分区，指定服务器的新用户分配随机数
	 * 
	 * @param area
	 * @param machine
	 * @return
	 */
	public ServerInfo getConfig(String area, String group, String machine) {
		Map<String, Map<Integer, ServerInfo>> groupMap = serverListMap.get(area);
		if (null != groupMap) {
			Map<Integer, ServerInfo> randomMap = groupMap.get(group);
			if (null != randomMap) {
				return randomMap.get(machine);
			}
		}
		return null;
	}

	public PropertiesConfiguration getVersionConfig() {
		return versionConfig;
	}

	public List<String> getConfigList() {
		return configList;
	}

	public PropertiesConfiguration getRnuConfig() {
		return rnuConfig;
	}
	/**
	 * 获取服务器配置列表
	 */
	public Map<String, Map<String, Map<Integer, ServerInfo>>> getServerListMap() {
		return serverListMap;
	}

}
