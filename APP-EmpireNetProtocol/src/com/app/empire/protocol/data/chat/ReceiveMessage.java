package com.app.empire.protocol.data.chat;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class ReceiveMessage extends AbstractData {
	private int chatType;// 聊天类型：0普通聊天 1普通公告
	private int chatSubType;// 聊天子协议：0普通聊天（1：普通聊天，2：彩色聊天） 1普通公告（1：普通公告，2：彩色公告）
	private int channelId;// 频道id 1系统类型，２世界聊天类型，３公会聊天类型，４私聊
	private int sendId;// 发送者id
	private String sendName;// 发送者昵称
	private int reveId;// 接收者id
	private String reveName;// 接受者昵称
	private String message;// 消息
	private String time;// 时间


	public ReceiveMessage(int sessionId, int serial) {
		super(Protocol.MAIN_CHAT, Protocol.CHAT_ReceiveMessage, sessionId, serial);
	}

	public ReceiveMessage() {
		super(Protocol.MAIN_CHAT, Protocol.CHAT_ReceiveMessage);
	}

	public int getChatType() {
		return chatType;
	}

	public void setChatType(int chatType) {
		this.chatType = chatType;
	}

	public int getChatSubType() {
		return chatSubType;
	}

	public void setChatSubType(int chatSubType) {
		this.chatSubType = chatSubType;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public int getSendId() {
		return sendId;
	}

	public void setSendId(int sendId) {
		this.sendId = sendId;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public int getReveId() {
		return reveId;
	}

	public void setReveId(int reveId) {
		this.reveId = reveId;
	}

	public String getReveName() {
		return reveName;
	}

	public void setReveName(String reveName) {
		this.reveName = reveName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
 

}
