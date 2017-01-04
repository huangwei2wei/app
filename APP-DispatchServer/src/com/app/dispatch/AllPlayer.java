package com.app.dispatch;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

public class AllPlayer {
	private static AllPlayer allPlayer = new AllPlayer();
	private ConcurrentHashMap<Integer, IoSession> sessions = new ConcurrentHashMap<Integer, IoSession>(); // sessionID-->客户端IoSession

	// private ConcurrentHashMap<Integer, ClientInfo> playerId2Client = new ConcurrentHashMap<Integer, ClientInfo>();// playerId-->客户端ClientInfo

	private AllPlayer() {

	}

	public static AllPlayer getAllPlayer() {
		return allPlayer;
	}

	public ConcurrentHashMap<Integer, IoSession> getSessions() {
		return sessions;
	}

	// public ConcurrentHashMap<Integer, ClientInfo> getPlayerId2Client() {
	// return playerId2Client;
	// }

}
