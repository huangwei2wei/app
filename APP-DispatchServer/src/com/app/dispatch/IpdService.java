package com.app.dispatch;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.app.empire.protocol.data.server.SyncLoad;
import com.app.empire.protocol.data.server.UpdateServerInfo;

public class IpdService implements Runnable {
	private BlockingQueue<SyncLoad> messages = new LinkedBlockingQueue<SyncLoad>();
	private static final Logger log = Logger.getLogger(IpdService.class);
	private IpdConnector connector;
	private String mode;
	private Configuration configuration;
	/**
	 * 初始化IP分配器，并连接IpdService服务器
	 * 
	 * @param configuration
	 */
	public IpdService(Configuration configuration) {
		this.configuration = configuration;
		String ip = configuration.getString("ipdServer");
		int port = configuration.getInt("ipdPort");
		this.connector = new IpdConnector("Ipd Connector", new InetSocketAddress(ip, port));
		this.mode = configuration.getString("servertype");
		if (!(this.mode.equals("singlesocket")))
			ipdConnect();
	}

	/**
	 * 连接IpdService服务器
	 */
	public void ipdConnect() {
		try {
			if (this.connector.isConnected())
				this.connector.close();
			this.connector.connect();
			new Thread(this, "IPD Service").start();

			if (this.connector.isConnected())
				System.out.println("ipd 服务链接成功！");
			else
				System.out.println("ipd 服务链接失败！");
		} catch (ConnectException e) {
			log.warn("分配器连接失败！");
		}
	}
	
	/**
	 * 通知ipd 在线人数等消息
	 */
	public void notifyIPD(int current, int maxPlayer, boolean maintance) throws Exception {
		if ((this.connector != null) && (this.connector.isConnected())) {
			SyncLoad notify = new SyncLoad();
			notify.setCurrOnline(current);
			notify.setMaxOnline(maxPlayer);
			notify.setMaintance(maintance);
			this.messages.put(notify);
		}
	}

	/**
	 * 发送数据给ipdservice
	 * 
	 * @param data
	 */
	public void updateServerInfo(String area, String group, int machineId, String version, String updateurl, String remark, String appraisal) {
		UpdateServerInfo dataInfo = new UpdateServerInfo();
		dataInfo.setArea(area);
		dataInfo.setGroup(group);
		dataInfo.setMachineId(machineId);
		dataInfo.setLine(DisServer.configuration.getConfiguration().getInt("id"));// 线id
		dataInfo.setVersion(version);
		dataInfo.setUpdateurl(updateurl);
		dataInfo.setRemark(remark);
		dataInfo.setAppraisal(appraisal);

		String ip = this.configuration.getString("publicserver");
		int port = this.configuration.getInt("publicport");
		String address = ip + ":" + port;

		dataInfo.setAddress(address);
		this.connector.send(dataInfo);
	}

	public void run() {
		while (true) {
			try {
				SyncLoad notify = (SyncLoad) this.messages.take();
				this.connector.send(notify);
			} catch (InterruptedException ex1) {
				log.debug(ex1, ex1);
			}
		}
	}
}