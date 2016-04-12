package com.app.empire.protocol.data.chat;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class SendMessage extends AbstractData {
	private int channelId;// 要添加的频道id 1系统类型，２世界聊天类型，３公会聊天类型，４私聊
	private String message;// 消息
	private int playerId; // 信息接收人ID

	public SendMessage(int sessionId, int serial) {
		super(Protocol.MAIN_CHAT, Protocol.CHAT_SendMessage, sessionId, serial);
	}

	public SendMessage() {
		super(Protocol.MAIN_CHAT, Protocol.CHAT_SendMessage);
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
}
