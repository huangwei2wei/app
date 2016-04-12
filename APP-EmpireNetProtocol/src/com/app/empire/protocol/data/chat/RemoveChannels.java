package com.app.empire.protocol.data.chat;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 移除频道
 * 
 * @author doter
 * 
 */
public class RemoveChannels extends AbstractData {
	private String channelsName;// 通道名称

	public RemoveChannels(int sessionId, int serial) {
		super(Protocol.MAIN_CHAT, Protocol.CHAT_RemoveChannels, sessionId, serial);
	}
	public RemoveChannels() {
		super(Protocol.MAIN_CHAT, Protocol.CHAT_RemoveChannels);
	}

	public String getChannelsName() {
		return channelsName;
	}
	public void setChannelsName(String channelsName) {
		this.channelsName = channelsName;
	}

}
