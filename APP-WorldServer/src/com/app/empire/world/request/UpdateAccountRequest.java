package com.app.empire.world.request;

import com.app.session.Session;

public class UpdateAccountRequest extends SessionRequest {
	private int updateType;
	private int accountId;
	private int playerId;
	private String[] values;

	public UpdateAccountRequest(int id, int sessionId, Session session, int updateType, String[] values, int accountId, int playerId) {
		super(IRequestType.UPDATE_ACCOUNT, id, sessionId, session);
		this.updateType = updateType;
		this.accountId = accountId;
		this.playerId = playerId;
		this.values = values;
	}

	public int getUpdateType() {
		return updateType;
	}

	public void setUpdateType(int updateType) {
		this.updateType = updateType;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
}