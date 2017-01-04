package com.app.empire.scene.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.app.empire.scene.SceneServer;
import com.app.empire.scene.session.ConnectSession;
import com.app.session.Session;
import com.google.protobuf.Message;

public class BroadUtil {
	/**
	 * 广播数据给玩家
	 * 
	 * @param players
	 * @param type
	 * @param subType
	 * @param msg
	 * @return
	 */
	public static void sendBroadcastPacket(Set<Integer> players, short type, short subType, Message msg) {
		ConcurrentHashMap<Integer, Session> sessions = SceneServer.getRegistry().getSessionID2Session();
		for (Session session : sessions.values()) {
			((ConnectSession) session).sendBroadcastPacket(players, type, subType, msg);
		}
	}

	/**
	 * 发送数据到 worldServer
	 * 
	 * @param type
	 * @param subType
	 * @param sessionId
	 * @param msg
	 */
	public static void sendMsg2worldServer(short type, short subType, int sessionId, Message msg) {
		ConcurrentHashMap<Integer, Session> sessions = SceneServer.getRegistry().getSessionID2Session();
		for (Session session : sessions.values()) {
			((ConnectSession) session).send2WorldServer(type, subType, sessionId, msg);
			return;
		}
	}

}
