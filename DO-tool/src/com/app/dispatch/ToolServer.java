package com.app.dispatch;

import org.apache.log4j.Logger;
import com.app.empire.protocol.Protocol;
import com.app.net.ProtocolFactory;

public class ToolServer {
	private static final Logger log = Logger.getLogger(ToolServer.class);
	/** 配置信息 */
	public static ConfigMenger configuration = null;

	public static void main(String[] args) throws Throwable {
		ToolServer main = new ToolServer();
		main.launch();
	}

	private void launch() throws Exception {
		// 初始化协议接口
		ProtocolFactory.init(Protocol.class, "com.app.empire.protocol.data", "com.app.handler");
		// 加载配置文件
		configuration = new ConfigMenger("configDispatch.properties");
		long a = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			// System.out.println(i);
			new ipdServer(configuration.getConfiguration());
		}

		long b = System.currentTimeMillis();
		System.out.println("用时：" + (b - a));
		// Thread t = new Thread(new StatisticsServer());
		// t.start();

	}
}