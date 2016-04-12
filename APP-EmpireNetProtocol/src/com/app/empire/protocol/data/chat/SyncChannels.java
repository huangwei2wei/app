package com.app.empire.protocol.data.chat;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * (同步玩家频道设置)对应数据封装。 实现频道切换
 * 
 * @see AbstractData
 * @author doter
 */
public class SyncChannels extends AbstractData {
	private int toSession;// 玩家sessionId
	private String[] add;
	private String[] remove;

	public SyncChannels(int sessionId, int serial) {
		super(Protocol.MAIN_CHAT, Protocol.CHAT_SyncChannels, sessionId, serial);
	}

	public SyncChannels() {
		super(Protocol.MAIN_CHAT, Protocol.CHAT_SyncChannels);
	}

	public int getToSession() {
		return this.toSession;
	}

	public void setToSession(int toSession) {
		this.toSession = toSession;
	}

	public String[] getAdd() {
		return this.add;
	}

	public void setAdd(String[] add) {
		this.add = add;
	}

	public String[] getRemove() {
		return this.remove;
	}

	public void setRemove(String[] remove) {
		this.remove = remove;
	}
}
