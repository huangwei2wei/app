package com.app.dispatch;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.buffer.IoBuffer;
import com.app.dispatch.vo.ClientInfo;
import com.app.dispatch.vo.Hero;
import com.app.dispatch.vo.Player;
import com.app.empire.protocol.data.syn.ReturnAttack;
import com.app.empire.protocol.data.syn.ReturnMove;
import com.app.protocol.INetData;
import com.app.protocol.ProtocolManager;

/***
 * PVP 同步服务
 * 
 * @author doter
 */

public class PvpService {
	/**
	 * 广播移动数据到房间内的玩家
	 * 
	 * @param clientInfo
	 * @param id 玩家单位id（如英雄流水id
	 * @param channelService
	 * @throws IllegalAccessException
	 */
	public void broadcastingMove(ClientInfo clientInfo, INetData udata, ChannelService channelService) throws IllegalAccessException {
		Player player = clientInfo.getPlayer();
		int roomId = player.getRoomId();
		int id = udata.readInt();// 玩家单位id（如英雄流水id
		byte direction = udata.readByte();// 方向1-12
		int x = udata.readInt();// 现在位置
		int y = udata.readInt();// 现在位置
		int toX = udata.readInt();// 所在宽度位置
		int toY = udata.readInt();// 所在高度位置

		ReturnMove returnMove = new ReturnMove();
		returnMove.setPlayerId(player.getPlayerId());
		returnMove.setId(id);
		returnMove.setDirection(direction);
		returnMove.setX(x);
		returnMove.setY(y);
		returnMove.setToX(toX);
		returnMove.setToY(toY);
		Channel channel = channelService.getChannel("PVP_ROOM_" + roomId);
		channel.broadcast(IoBuffer.wrap(ProtocolManager.makeSegment(returnMove).getPacketByteArray()));

		Hero hero = player.getHero().get(id);
		hero.setX(x);
		hero.setY(y);
		hero.setToX(toX);
		hero.setToY(toY);
		hero.setDirection(direction);
	}
	/**
	 * 广播技能，攻击等
	 * 
	 * @param clientInfo
	 * @param id
	 * @param channelService
	 * @throws IllegalAccessException
	 */
	public void broadcastingAttack(ClientInfo clientInfo, INetData data, ChannelService channelService, ConcurrentHashMap<Integer, ClientInfo> allClientInfo) throws IllegalAccessException {
		Player player = clientInfo.getPlayer();
		int roomId = player.getRoomId();
		int id = data.readInt();// 攻击方玩家单位id（如英雄流水id
		byte direction = data.readByte();// 人物朝向 1-12
		int x = data.readInt();// 人物位置
		int y = data.readInt();// 人物位置
		int z = data.readInt();// 人物位置
		int skillId = data.readInt();// 技能id
		int[] playerId = data.readInts();// 被攻击方角色id
		int[] heroId = data.readInts();// 被攻击方玩家单位id（如英雄流水id
		int[] hurt = data.readInts();// 分别掉血量

		ReturnAttack returnAttack = new ReturnAttack();
		returnAttack.setId(id);
		returnAttack.setHeroId(heroId);
		returnAttack.setDirection(direction);
		returnAttack.setX(x);
		returnAttack.setY(y);
		returnAttack.setZ(z);
		returnAttack.setSkillId(skillId);
		returnAttack.setHurt(hurt);
		Channel channel = channelService.getChannel("PVP_ROOM_" + roomId);
		channel.broadcast(IoBuffer.wrap(ProtocolManager.makeSegment(returnAttack).getPacketByteArray()));
		// 计算单位的剩余血量
		for (int i = 0; i < playerId.length; i++) {
			int playerId2 = playerId[i];
			int heroId2 = heroId[i];
			int hurt2 = hurt[i];
			ClientInfo ClientInfo = allClientInfo.get(playerId2);
			Hero hero = ClientInfo.getPlayer().getHero().get(heroId2);
			hero.setHp(hero.getHp() - hurt2);
		}
	}
}
