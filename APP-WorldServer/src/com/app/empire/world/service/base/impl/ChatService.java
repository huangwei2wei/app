package com.app.empire.world.service.base.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.app.empire.protocol.data.chat.ReceiveMessage;
import com.app.empire.protocol.data.chat.SyncChannels;
import com.app.empire.protocol.data.server.BroadCast;
import com.app.empire.world.common.util.Common;
import com.app.empire.world.common.util.DateUtil;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mysql.gameConfig.BaseLanguage;
import com.app.empire.world.exception.ErrorMessages;
import com.app.empire.world.exception.TipMessages;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.ProtocolManager;

/**
 * 聊天服务
 * 
 * @author doter
 */
@Service
public class ChatService {
	Logger log = Logger.getLogger(ChatService.class);

	/**
	 * 初始化玩家聊天频道(世界、公会等
	 * 
	 * @param player 玩家信息
	 */
	public void initPlayerChannels(ConnectSession session, WorldPlayer worldPlayer) {
		Player player = worldPlayer.getPlayer();
		int guildId = player.getGuildId();
		List<String> addchannel = new ArrayList<String>();
		// addchannel.add(Common.CHAT_WORLD_CHANNEL);
		if (guildId > 0)
			addchannel.add(Common.CHAT_GUILD_CHANNEL + "_" + guildId);
		if (addchannel.size() > 0)
			this.syncChannels(session, player.getId(), addchannel.toArray(new String[addchannel.size()]), new String[0]);
	}

	/**
	 * 同步玩家频道设置
	 * 
	 * @param session
	 * @param playerId
	 * @param addChannels
	 * @param removeChannels
	 */
	public void syncChannels(ConnectSession session, int playerId, String[] addChannels, String[] removeChannels) {
		if (session == null)
			return;
		SyncChannels sync = new SyncChannels();
		sync.setToSession(playerId);
		sync.setAdd(addChannels);
		sync.setRemove(removeChannels);
		session.write(sync);
	}
	/**
	 * 发送信息给指定玩家
	 * 
	 * @param receiveMessage
	 * @param player 信息发送玩家
	 * @param playerName 信息目标玩家名称
	 * @param playerId 　接受者角色id
	 */
	public void sendMessageToPlayer(ReceiveMessage receiveMessage, WorldPlayer player, int playerId) {
		receiveMessage.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		WorldPlayer receivePlayer = ServiceManager.getManager().getPlayerService().getPlayer(playerId);
		if (receivePlayer == null) {// 目标玩家不存在
			receiveMessage.setChannelId(Common.CHAT_WHISPER);
			receiveMessage.setSendName(TipMessages.SYSNAME_MESSAGE);
			receiveMessage.setReveId(player.getPlayer().getId());
			receiveMessage.setReveName(player.getName());
			receiveMessage.setMessage(TipMessages.PLAYER + ErrorMessages.CHAT_NOTONLINE);
			player.sendData(receiveMessage);
			return;
		}
		Date gagEndTime = player.getPlayer().getGagEndTime();
		if (gagEndTime != null && gagEndTime.getTime() > System.currentTimeMillis()) {// 玩家禁言
			Map<Integer, Map> lan = ServiceManager.getManager().getGameConfigService().getGameConfig().get(BaseLanguage.class.getSimpleName());
			receiveMessage.setChannelId(Common.CHAT_SYSTEM);
			receiveMessage.setSendName(TipMessages.SYSNAME_MESSAGE);
			receiveMessage.setReveId(player.getPlayer().getId());
			receiveMessage.setReveName(player.getName());
			receiveMessage.setMessage(lan.get(3).get("msg").toString());// 禁言中
			player.sendData(receiveMessage);
			return;
		}
		receiveMessage.setSendId(player.getPlayer().getId());
		receiveMessage.setSendName(player.getPlayer().getNickname());
		receiveMessage.setReveId(playerId);
		receiveMessage.setReveName(receivePlayer.getPlayer().getNickname());
		// player.sendData(receiveMessage);
		receivePlayer.sendData(receiveMessage);
	}
	/**
	 * 发送信息给世界频道内的所有玩家
	 * 
	 * @param receiveMessage
	 * @param player
	 */
	public void sendMessageToWorld(ReceiveMessage receiveMessage, WorldPlayer player) {
		receiveMessage.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		receiveMessage.setChannelId(Common.CHAT_WORLD);
		receiveMessage.setSendId(player.getPlayer().getId());
		receiveMessage.setSendName(player.getName());
		receiveMessage.setReveName(" ");

		Date gagEndTime = player.getPlayer().getGagEndTime();
		if (gagEndTime != null && gagEndTime.getTime() > System.currentTimeMillis()) {// 玩家禁言
			Map<Integer, Map> lan = ServiceManager.getManager().getGameConfigService().getGameConfig().get(BaseLanguage.class.getSimpleName());
			receiveMessage.setChannelId(Common.CHAT_SYSTEM);
			receiveMessage.setSendName(TipMessages.SYSNAME_MESSAGE);
			receiveMessage.setReveId(player.getPlayer().getId());
			receiveMessage.setReveName(player.getName());
			receiveMessage.setMessage(lan.get(3).get("msg").toString());// 禁言中
			player.sendData(receiveMessage);
			return;
		} else {
			receiveMessage.setSendId(player.getPlayer().getId());
			BroadCast broadCast = new BroadCast();
			broadCast.setChannel(Common.CHAT_WORLD_CHANNEL);
			broadCast.setData(ProtocolManager.makeSegment(receiveMessage).getPacketByteArray());
			ServiceManager.getManager().getConnectService().broadcast(broadCast);
		}
	}

	/**
	 * 发送信息给公会中的所有玩家
	 * 
	 * @param receiveMessage
	 * @param player
	 */
	public void sendMessageToGuild(ReceiveMessage receiveMessage, WorldPlayer player) {
		int guildId = player.getPlayer().getGuildId();
		if (guildId > 0) {
			receiveMessage.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			receiveMessage.setChannelId(Common.CHAT_WORLD);
			receiveMessage.setSendId(player.getPlayer().getId());
			receiveMessage.setSendName(player.getName());
			receiveMessage.setReveName(" ");

			Date gagEndTime = player.getPlayer().getGagEndTime();
			if (gagEndTime != null && gagEndTime.getTime() > System.currentTimeMillis()) {// 玩家禁言
				Map<Integer, Map> lan = ServiceManager.getManager().getGameConfigService().getGameConfig().get(BaseLanguage.class.getSimpleName());
				receiveMessage.setChannelId(Common.CHAT_SYSTEM);
				receiveMessage.setSendName(TipMessages.SYSNAME_MESSAGE);
				receiveMessage.setReveId(player.getPlayer().getId());
				receiveMessage.setReveName(player.getName());
				receiveMessage.setMessage(lan.get(3).get("msg").toString());// 禁言中
				player.sendData(receiveMessage);
				return;
			} else {
				receiveMessage.setSendId(player.getPlayer().getId());
				BroadCast broadCast = new BroadCast();
				broadCast.setChannel(Common.CHAT_GUILD_CHANNEL + "_" + guildId);
				broadCast.setData(ProtocolManager.makeSegment(receiveMessage).getPacketByteArray());
				ServiceManager.getManager().getConnectService().broadcast(broadCast);
			}
		}
	}

	/**
	 * 发送系统信息给指定玩家
	 * 
	 * @param worldPlayer
	 * @param content
	 */
	public void sendSystemMessage(WorldPlayer worldPlayer, String content) {
		ReceiveMessage receiveMessage = new ReceiveMessage();
		receiveMessage.setChatType(0);
		receiveMessage.setChatSubType(1);
		receiveMessage.setTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		receiveMessage.setSendName(TipMessages.SYSNAME_MESSAGE);
		receiveMessage.setReveName(worldPlayer.getName());
		receiveMessage.setMessage(content);
		worldPlayer.sendData(receiveMessage);
	}

	//
	// /**
	// * 发送信息给公会成员
	// *
	// * @param receiveMessage
	// * @param player
	// */
	// public void sendMessageToGuild(ReceiveMessage receiveMessage, WorldPlayer
	// player) {
	// receiveMessage.setTime(getTime());
	// if (0 == player.getGuildId()) {
	// receiveMessage.setChannel(CHANNEL_GUILD);
	// receiveMessage.setSendName(TipMessages.SYSNAME_MESSAGE);
	// receiveMessage.setReveId(player.getId());
	// receiveMessage.setReveName(player.getName());
	// receiveMessage.setMessage(ErrorMessages.CHAT_COMMUNITY);
	// player.sendData(receiveMessage);
	// } else {
	// receiveMessage.setSendId(player.getId());
	// if (ChatService.CHAT_STATUS1 != player.getPlayer().getChatStatus()
	// && player.getPlayer().getProhibitTime().getTime() >
	// System.currentTimeMillis()) {
	// player.sendData(receiveMessage);
	// return;
	// }
	// BroadCast broadCast = new BroadCast();
	// broadCast.setChannel(CHAT_GUILD_CHANNEL + "_" + player.getGuildId());
	// broadCast.setData(ProtocolManager.makeSegment(receiveMessage).getPacketByteArray());
	// ServiceManager.getManager().getConnectService().broadcast(broadCast);
	// }
	// }
	//

	//
	// /**
	// * vip上线公告
	// *
	// * @param player
	// */
	// public void wellcomVIP(WorldPlayer player) {
	// if (player.isVip() && player.getPlayer().getVipLevel() > 8) {
	// String[] contents = TipMessages.VIPONLINEBULLETIN.split("&");
	// String content = contents[ServiceUtils.getRandomNum(0, contents.length)];
	// content = content.replace("***",
	// player.getPlayer().getVipLevel().toString());
	// content = content.replace("###", player.getName());
	// sendBulletinToWorld(content, null, true);
	// }
	// }
	//

}
