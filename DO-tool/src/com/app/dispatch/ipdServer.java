package com.app.dispatch;

import java.net.InetSocketAddress;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

public class ipdServer {

	private static final Logger log = Logger.getLogger(ipdServer.class);
	private IpdConnector connector;
	private String mode;
	private Configuration configuration;

	/**
	 * 初始化IP分配器，并连接IpdService服务器
	 * 
	 * @param configuration
	 */
	public ipdServer(Configuration configuration) {
		this.configuration = configuration;
		String ip = configuration.getString("ip");
		int port = configuration.getInt("port");
		this.connector = new IpdConnector("Ipd Connector", new InetSocketAddress(ip, port));
		this.mode = configuration.getString("servertype");
		// ipdConnect();
		new Thread(new ConnectDis()).start();
	}

	/**
	 * 连接IpdService服务器
	 */
	public void ipdConnect() {
		try {
			if (this.connector.isConnected())
				this.connector.close();
			this.connector.connect();

			if (!this.connector.isConnected())
				System.out.println("服务链接失败！");
			// sendData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ConnectDis implements Runnable {
		@Override
		public void run() {
			try {
				 ipdServer.this.connector.connect();
//				ipdServer.this.ipdConnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}