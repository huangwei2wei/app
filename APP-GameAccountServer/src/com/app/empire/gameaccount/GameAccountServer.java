package com.app.empire.gameaccount;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.app.empire.gameaccount.service.factory.ServiceFactory;
import com.app.empire.protocol.Protocol;
import com.app.net.ProtocolFactory;

public class GameAccountServer {
	private static final Logger log = Logger.getLogger(GameAccountServer.class);
	private ApplicationContext context;

	public void launch() throws Exception {
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
		ProtocolFactory.init(Protocol.class, "com.app.empire.protocol.data", "com.app.empire.gameaccount.handler");
		ServiceFactory sf = context.getBean(ServiceFactory.class);
		sf.setServiceFactory(sf);// 因为herder处理是new出来的
		sf.getWorldStub().start();
		System.out.println("游戏分区帐号数据服务器启动...");
	}

	public static void main(String[] args) throws Exception {
		GameAccountServer server = new GameAccountServer();
		server.launch();
	}
}
