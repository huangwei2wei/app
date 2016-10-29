package com.app.dispatch;
import java.net.InetSocketAddress;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import com.app.empire.protocol.data.account.Login;
import com.app.net.Connector;
import com.app.protocol.data.DataBeanFilter;
import com.app.protocol.s2s.S2SDecoder;
import com.app.protocol.s2s.S2SEncoder;
public class IpdConnector extends Connector {
	private static final Logger log = Logger.getLogger(IpdConnector.class);

	/**
	 * 初始化Connector
	 * 
	 * @param id
	 * @param address
	 * @param configuration
	 */
	public IpdConnector(String id, InetSocketAddress address) {
		super(id, address);
	}

	/**
	 * 初始化过滤器
	 */
	public void init() {
		this.connector.getFilterChain().addLast("wyd2codec", new ProtocolCodecFilter(new S2SEncoder(), new S2SDecoder()));
		this.connector.getFilterChain().addLast("wyd2databean", new DataBeanFilter());
	}

	/**
	 * 连接IPD Server服务器<br>
	 * 发送游戏登录服务器 id，名称，地址<br>
	 * 发送在线人数，最大人数限制，服务器状态
	 */
	@Override
	protected void connected() {
		sendData();
	}

	@Override
	protected void idle() {
		// TODO Auto-generated method stub
	}
	public void sendData() {
		try {
			System.out.println("发送登录");
			String uuid = UUID.randomUUID().toString();
			Login login = new Login();
			login.setAccountName(uuid);
			//login.setAccountName("af6b0351-e8cf-49f2-a026-e8");
			// login.setAccountName("af6b0351");
			login.setPassWord("123456");
			login.setVersion("1.0.0.0");
			login.setChannel(1);
			login.setClientModel("htc");
			login.setSystemName("Andro");
			login.setSystemVersion("1.0.0.1");
			this.send(login);
			StatisticsServer.getStatisticsServer().getReqNum().getAndIncrement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}