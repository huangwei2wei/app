package com.app.empire.protocol.data.account;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class UpdatePlayerData extends AbstractData {
	private String[] key;// 推送的key
	private String[] value;// 推送的值

	public UpdatePlayerData(int sessionId, int serial) {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_UpdatePlayerData, sessionId, serial);
	}

	public UpdatePlayerData() {
		super(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_UpdatePlayerData);
	}

	public String[] getKey() {
		return key;
	}

	public void setKey(String[] key) {
		this.key = key;
	}

	public String[] getValue() {
		return value;
	}

	public void setValue(String[] value) {
		this.value = value;
	}

}
