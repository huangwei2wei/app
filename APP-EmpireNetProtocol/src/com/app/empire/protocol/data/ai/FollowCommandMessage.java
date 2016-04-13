package com.app.empire.protocol.data.ai;

import com.app.empire.protocol.Protocol;

public class FollowCommandMessage extends CommandMessage {
	private long oid;// 跟随单位OID的id
	private Integer speed;// 速度
	private Float distanceToFollowAt;// 和被跟随者的距离

	public FollowCommandMessage(int sessionId, int serial) {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackList, sessionId, serial);
	}
	public FollowCommandMessage() {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackList);
	}

	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public Integer getSpeed() {
		return speed;
	}
	public void setSpeed(Integer speed) {
		this.speed = speed;
	}
	public Float getDistanceToFollowAt() {
		return distanceToFollowAt;
	}
	public void setDistanceToFollowAt(Float distanceToFollowAt) {
		this.distanceToFollowAt = distanceToFollowAt;
	}

}
