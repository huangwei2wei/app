package com.app.empire.protocol.data.ai;

import com.app.empire.protocol.Protocol;

public class GotoCommandMessage extends CommandMessage {
	private float x;// Point x
	private float y;// Point y
	private float z;// Point z
	private float speed;// 速度

	public GotoCommandMessage(int sessionId, int serial) {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackList, sessionId, serial);
	}

	public GotoCommandMessage() {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackList);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
