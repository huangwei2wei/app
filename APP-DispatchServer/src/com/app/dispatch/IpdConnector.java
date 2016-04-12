package com.app.dispatch;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import com.app.empire.protocol.data.server.Heartbeat;
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
		this.config.setIdleTime(IdleStatus.BOTH_IDLE,120);
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
	}

	@Override
	protected void idle() {
		Heartbeat heart = new Heartbeat();
		send(heart);
	}

}