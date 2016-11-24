package com.app.empire.scene.session;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.app.empire.protocol.data.error.ProtocolError;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.session.Session;

/**
 * 类ConnectSession 客户sesson自定义封装应用，继承 Session 类
 * 
 * @see Session
 * @author doter
 */
public class ConnectSession extends Session {
	private static final Logger						log				= Logger.getLogger(ConnectSession.class);
	private String									name;

	private ConcurrentHashMap<Integer, ArmyProxy>	playerid2Army	= new ConcurrentHashMap<Integer, ArmyProxy>();	// 链接时

	public ConnectSession(IoSession session) {
		super(session);
	}

	@Override
	public void created() {
	}

	@Override
	public void closed() {
		// 场景服务器关闭时的操作
	}

	@Override
	public <T> void handle(T paramT) {

	}

	@Override
	public void idle(IoSession session, IdleStatus status) {
		System.out.println("关闭链接：" + session);
		session.close(true);
	}

	@Override
	public void opened() {

	}

	/**
	 * 根据玩家id发送对应数据包
	 * 
	 * @param seg
	 * @param playerId
	 */
	public void write(AbstractData seg, int playerId) {
		ArmyProxy army = this.playerid2Army.get(playerId);
		if (army != null) {
			seg.setSessionId(army.getSessionId().intValue());
			write(seg);
		} else {
			log.error("玩家不在线：" + playerId);
		}
	}

	/**
	 * 拋出异常
	 */
	@Override
	public void sendError(ProtocolException ex) {
		ProtocolError seg = new ProtocolError(ex);
		write(seg);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * 游戏角色登录。
	 * 
	 * @param player
	 * @param data
	 * @param client
	 * @param relogin
	 * @return
	 * @throws Exception
	 */
	public void playerLogin(ArmyProxy army) {
		this.playerid2Army.put(army.getPlayer().getId(), army);
	}

	/**
	 * 玩家退出
	 * 
	 * @param army
	 */
	public void playerLoginOut(ArmyProxy army) {
		this.playerid2Army.remove(army.getPlayer().getId());
	}

}