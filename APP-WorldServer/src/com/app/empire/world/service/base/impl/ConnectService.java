package com.app.empire.world.service.base.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Service;

import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.session.Session;
import com.app.session.SessionRegistry;

@Service
public class ConnectService implements Runnable {
	private SessionRegistry registry;

	public void start() {
		Thread t = new Thread(this);
		t.setName("ConnectService-Thread");
		t.start();
	}

	/** 广播数据到所有的dis */
	public void broadcast(AbstractData seg) {
		ConcurrentHashMap<IoSession, Session> sessionMap = registry.getIoSession2Session();
		for (Session session : sessionMap.values()) {
			session.write(seg);
		}
	}

	/**
	 * 发送数据给指定玩家
	 * 
	 * @param seg
	 * @param playerId
	 */
	public void writeTo(AbstractData seg, int playerId) {
		ConcurrentHashMap<IoSession, Session> sessionMap = registry.getIoSession2Session();
		for (Session session : sessionMap.values()) {
			((ConnectSession) session).write(seg, playerId);
		}
	}
	/** 重启dis */
	public void shutdown() {
		ConcurrentHashMap<IoSession, Session> sessionMap = registry.getIoSession2Session();
		for (Session session : sessionMap.values()) {
			((ConnectSession) session).shutdown();
		}
	}

	public void logOnline() {
		ConcurrentHashMap<IoSession, Session> sessionMap = registry.getIoSession2Session();
		for (Session session : sessionMap.values()) {
			((ConnectSession) session).loginOnline();
		}
	}

	public int getOnline() {
		int playerNum = 0;
		ConcurrentHashMap<IoSession, Session> sessionMap = registry.getIoSession2Session();
		for (Session session : sessionMap.values()) {
			playerNum += ((ConnectSession) session).getPlayerCount();
		}
		return playerNum;
	}

	/**
	 * 根据账号id，注销账号
	 * 
	 * @param accountId
	 *            据账号id
	 */
	public void forceLogout(int accountId) {
		ConcurrentHashMap<IoSession, Session> sessionMap = registry.getIoSession2Session();
		for (Session session : sessionMap.values()) {
			((ConnectSession) session).forceLogout(accountId);
		}
	}
	 /** 禁止玩家上线*/
	public void kick(int playerId) {
		ConcurrentHashMap<IoSession, Session> sessionMap = registry.getIoSession2Session();
		for (Session session : sessionMap.values()) {
			((ConnectSession) session).kick(playerId);
		}
	}
	/**
	 * 通知dispatcher server 服务器最大玩家人数信息
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(60000L);
			} catch (InterruptedException ex) {
			}
			try {
				ConcurrentHashMap<IoSession, Session> sessionMap = registry.getIoSession2Session();
				for (Session session : sessionMap.values()) {
					((ConnectSession) session).notifyMaxPlayer();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void setRegistry(SessionRegistry registry) {
		this.registry = registry;
	}

	public SessionRegistry getRegistry() {
		return registry;
	}

	// /**
	// * 更新服务器版本
	// */
	// public void UpdateVersion() {
	// UpdateServerInfo updateVers = new UpdateServerInfo();
	// updateVers.setArea(WorldServer.config.getArea());
	// updateVers.setGroup(WorldServer.config.getGroup());
	// updateVers.setMachine(WorldServer.config.getMachineCode() + "");
	// updateVers.setVersion(VersionUtils.select("num"));
	// updateVers.setUpdateurl(VersionUtils.select("updateurl"));
	// updateVers.setRemark(VersionUtils.select("remark"));
	// updateVers.setAppraisal(VersionUtils.select("appraisal"));
	// for (int i = 0; i < this.connects.length; ++i)
	// if (this.connects[i] != null)
	// this.connects[i].write(updateVers);
	// }
}