package com.app.dispatch;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.session.IoSession;

/**
 * 通道服务-一组session
 * 
 * @author doter
 * 
 */
public class ChannelService {
	private ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	private Channel worldChannel = new Channel("WORLD");

	public ChannelService() {
		this.channels.put(this.worldChannel.getName(), this.worldChannel);
	}

	public Channel getWorldChannel() {
		return this.worldChannel;
	}

	/**
	 * 获取或创建一个通道组
	 * 
	 * @param name
	 * @return
	 */
	public Channel getAndCreate(String name) {
		Channel channel = new Channel(name);
		Channel c = this.channels.putIfAbsent(name, channel);
		if (c == null) {
			c = channel;
		}
		return c;
	}

	/**
	 * 获取通道组
	 * 
	 * @param name
	 * @return
	 */
	public Channel getChannel(String name) {
		return this.channels.get(name);
	}

	/**
	 * 移除通道组
	 * 
	 * @param name
	 * @return
	 */
	public Channel removeChannel(String name) {
		return this.channels.remove(name);
	}

	/**
	 * 移除所有通道中的某个IoSession
	 * 
	 * @param session
	 */
	public void removeSessionFromAllChannel(IoSession session) {
		for (Channel channel : this.channels.values()) {
			int size = channel.removeSession(session);
			String name = channel.getName();
			if (size <= 0 && !name.equals(this.worldChannel.getName()))
				this.removeChannel(name);
		}
	}
}