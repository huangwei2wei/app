package com.app.dispatch.vo;

import java.util.Timer;
import org.apache.mina.core.session.IoSession;

public class ClientInfo {
	private long heartbeatTime = 0;// 心跳时间
	private int heartbeatCount = 0;// 心跳次数
	private long protocolTime = 0;// 协议时间
	private int protocolCount = 0;// 协议次数
	private long moveTime = 0;// 移动时间
	private int moveCount = 0;// 移动次数
	private IoSession ioSession;// 连接客户端的ioSession
	private Player player;
	private Timer timer = new Timer();
	private byte protocolType;// 上一次执行的协议
	private byte protocolSubType;// 上一次执行的协议

	public ClientInfo(IoSession ioSession) {
		this.ioSession = ioSession;
	}

	public long getHeartbeatTime() {
		return heartbeatTime;
	}

	public void setHeartbeatTime(long heartbeatTime) {
		this.heartbeatTime = heartbeatTime;
	}

	public int getHeartbeatCount() {
		return heartbeatCount;
	}

	public void setHeartbeatCount(int heartbeatCount) {
		this.heartbeatCount = heartbeatCount;
	}

	public long getProtocolTime() {
		return protocolTime;
	}

	public void setProtocolTime(long protocolTime) {
		this.protocolTime = protocolTime;
	}

	public int getProtocolCount() {
		return protocolCount;
	}

	public void setProtocolCount(int protocolCount) {
		this.protocolCount = protocolCount;
	}

	public IoSession getIoSession() {
		return ioSession;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public long getMoveTime() {
		return moveTime;
	}

	public void setMoveTime(long moveTime) {
		this.moveTime = moveTime;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}

	public void addHeartbeatCount() {
		this.heartbeatCount++;
	}
	public void addProtocolCount() {
		this.protocolCount++;
	}
	public void addMovecount() {
		this.moveCount++;
	}
	public Timer getTimer() {
		return timer;
	}

	public byte getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(byte protocolType) {
		this.protocolType = protocolType;
	}

	public byte getProtocolSubType() {
		return protocolSubType;
	}

	public void setProtocolSubType(byte protocolSubType) {
		this.protocolSubType = protocolSubType;
	}

}
