package com.app.empire.scene.session;

import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.app.empire.protocol.data.error.ProtocolError;
import com.app.empire.protocol.data.server.BroadPb;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.service.world.PlayerService;
import com.app.protocol.ProtocolManager;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.session.Session;
import com.google.protobuf.Message;

/**
 * 类ConnectSession 客户sesson自定义封装应用，继承 Session 类
 * 
 * @see Session
 * @author doter
 */
public class ConnectSession extends Session {
	private static final Logger log = Logger.getLogger(ConnectSession.class);
	private String name;
	private PlayerService playerService;

	public ConnectSession(IoSession session) {
		super(session);
	}

	@Override
	public void created() {
		System.out.println("dis 链接成功...");
	}

	@Override
	public void closed() {
		// 场景服务器关闭时的操作
		System.out.println("dis 断开链接...");
	}

	@Override
	public <T> void handle(T paramT) {

	}

	@Override
	public void idle(IoSession session, IdleStatus status) {
		System.out.println("关闭链接：" + session);
		log.info("关闭链接：" + session);
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
		if (this.playerService.isExist(playerId)) {
			seg.setSessionId(playerId);
			write(seg);
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
		this.playerService.addOnline(army);
	}

	/**
	 * 玩家退出
	 * 
	 * @param army
	 */
	public void playerLoginOut(int playerId) {
		this.playerService.unLine(playerId);
	}

	/**
	 * 广播数据给玩家
	 * 
	 * @param players 需要广播的玩家
	 * @param type 主协议
	 * @param subType 子协议
	 * @param msg 广播的数据
	 */
	public void sendBroadcastPacket(Set<Integer> players, short type, short subType, Message msg) {
		BroadPb broadPbMsg = new BroadPb();
		broadPbMsg.setPlayerId(ArrayUtils.toPrimitive(players.toArray(new Integer[players.size()])));

		PbAbstractData pbMsg = new PbAbstractData(type, subType, EnumTarget.CLIENT.getValue());
		pbMsg.setBytes(msg.toByteArray());
		broadPbMsg.setData(ProtocolManager.makeSegment(pbMsg).getPacketByteArray());

		write(broadPbMsg);
	}

	/**
	 * 发送数据到wroldServer
	 * 
	 * @param type
	 * @param subType
	 * @param sessionId
	 * @param serial
	 * @param msg
	 */
	public void send2WorldServer(short type, short subType, int sessionId, Message msg) {
		write(type, subType, sessionId, 0, msg, EnumTarget.WORLDSERVER.getValue());
	}

	public PlayerService getPlayerService() {
		return playerService;
	}

	public void setPlayerService(PlayerService playerService) {
		this.playerService = playerService;
	}

}