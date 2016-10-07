package com.app.empire.protocol.data.scene.world;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 登录场景服
 * 
 * @author doter
 * 
 */
public class LoginInOK extends AbstractData {
	private int result;// 登录状态
	private long time;// 当前时间

	public LoginInOK(int sessionId, int serial) {
		super(Protocol.MAIN_WORLD, Protocol.WORLD_LoginInOK, sessionId, serial);
	}

	public LoginInOK() {
		super(Protocol.MAIN_WORLD, Protocol.WORLD_LoginInOK);
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
