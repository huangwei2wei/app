package com.app.empire.world.service.base.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.empire.protocol.data.syn.Attack;
import com.app.empire.protocol.data.syn.Move;
import com.app.empire.protocol.data.syn.ReturnAttack;
import com.app.empire.protocol.data.syn.ReturnMove;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.model.RoomHeroEntitys;
import com.app.empire.world.model.RoomVo;
import com.app.empire.world.model.player.WorldPlayer;

@Service
public class PvpDisService {
	@Autowired
	private PvpRoomService pvpRoomService;

	/**
	 * 广播移动数据到房间内的玩家
	 * 
	 * @param clientInfo
	 * @param id 玩家单位id（如英雄流水id
	 * @param channelService
	 * @throws IllegalAccessException
	 */
	public void broadcastingMove(WorldPlayer worldPlayer, Move move) throws IllegalAccessException {
		Player player = worldPlayer.getPlayer();
		int playerId = player.getId();
		int roomType = player.getRoomType();
		int roomId = player.getRoomId();
		int heroId = move.getId();// 玩家单位id（如英雄流水id
		byte direction = move.getDirection();// 方向1-12
		int x = move.getX();// 现在位置
		int y = move.getY();// 现在位置
		int toX = move.getToX();// 目标位置
		int toY = move.getToY();// 目标位置

		ReturnMove returnMove = new ReturnMove();
		returnMove.setPlayerId(playerId);
		returnMove.setId(heroId);
		returnMove.setDirection(direction);
		returnMove.setX(x);
		returnMove.setY(y);
		returnMove.setToX(toX);
		returnMove.setToY(toY);

		RoomVo room = pvpRoomService.getRoom(roomType, roomId);
		pvpRoomService.broadcast(room, returnMove);
		RoomHeroEntitys entitys = room.getPlayerHero(playerId, heroId);
		entitys.setX(x);
		entitys.setY(y);
		entitys.setToX(toX);
		entitys.setToY(toY);
	}

	/**
	 * 广播技能，攻击等
	 * 
	 * @param clientInfo
	 * @param id
	 * @param channelService
	 * @throws IllegalAccessException
	 */
	public void broadcastingAttack(WorldPlayer worldPlayer, Attack data) throws IllegalAccessException {
		Player player = worldPlayer.getPlayer();
		int roomType = player.getRoomType();
		int roomId = player.getRoomId();
		int id = data.getId();// 攻击方玩家单位id（如英雄流水id
		byte direction = data.getDirection();// 人物朝向 1-12
		int x = data.getX();// 人物位置
		int y = data.getY();// 人物位置
		int z = data.getZ();// 人物位置
		int skillId = data.getSkillId();// 技能id
		int[] playerId = data.getPlayerId();// 被攻击方角色id
		int[] heroId = data.getHeroId();// 被攻击方玩家单位id（如英雄流水id
		int[] hurt = data.getHurt();// 分别掉血量

		ReturnAttack returnAttack = new ReturnAttack();
		returnAttack.setId(id);
		returnAttack.setHeroId(heroId);
		returnAttack.setDirection(direction);
		returnAttack.setX(x);
		returnAttack.setY(y);
		returnAttack.setZ(z);
		returnAttack.setSkillId(skillId);
		returnAttack.setHurt(hurt);
		RoomVo room = pvpRoomService.getRoom(roomType, roomId);
		pvpRoomService.broadcast(room, returnAttack);
		// 计算单位的剩余血量
		for (int i = 0; i < playerId.length; i++) {
			int playerId2 = playerId[i];
			int heroId2 = heroId[i];
			int hurt2 = hurt[i];
			RoomHeroEntitys entitys = room.getPlayerHero(playerId2, heroId2);
			int hp = entitys.getHp() - hurt2;
			entitys.setHp(hp);
		}
	}
}
