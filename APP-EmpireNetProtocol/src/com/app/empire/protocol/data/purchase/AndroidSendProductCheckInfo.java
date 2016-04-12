package com.app.empire.protocol.data.purchase;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class AndroidSendProductCheckInfo extends AbstractData {
	private String orderNum;
	private int playerId;
	private String key;
    private int channelId;

	public AndroidSendProductCheckInfo(int sessionId, int serial) {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_AndroidSendProductCheckInfo, sessionId, serial);
	}

	public AndroidSendProductCheckInfo() {
		super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_AndroidSendProductCheckInfo);
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }
}
