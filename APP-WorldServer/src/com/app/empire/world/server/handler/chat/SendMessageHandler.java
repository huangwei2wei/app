package com.app.empire.world.server.handler.chat;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.chat.ReceiveMessage;
import com.app.empire.protocol.data.chat.SendMessage;
import com.app.empire.world.common.util.Common;
import com.app.empire.world.common.util.KeywordsUtil;
import com.app.empire.world.exception.ErrorMessages;
import com.app.empire.world.logs.GameLogService;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.ChatService;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 转发聊天信息
 * 
 * @author doter
 */
public class SendMessageHandler implements IDataHandler {
	Logger log = Logger.getLogger(SendMessageHandler.class);

	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer player = session.getPlayer(data.getSessionId());
		SendMessage sendMessage = (SendMessage) data;
		try {
			long nowTime = System.currentTimeMillis();
			if ((nowTime - player.getLastSendMsgTime()) > 2000l) {
				player.setSendMsgCount(0);
			} else {
				player.setSendMsgCount(player.getSendMsgCount() + 1);
			}
			if (player.getSendMsgCount() > 3) {
				throw new ProtocolException(ErrorMessages.CHAT_OFTEN_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
			}
			player.setLastSendMsgTime(nowTime);
			String message = KeywordsUtil.filterKeywords(sendMessage.getMessage());
			ReceiveMessage receiveMessage = new ReceiveMessage();
			receiveMessage.setChannelId(sendMessage.getChannelId());
			receiveMessage.setSendName(player.getName());
			receiveMessage.setReveName("");
			receiveMessage.setMessage(message);
			receiveMessage.setChatType(0);
			receiveMessage.setChatSubType(1);
			String channel = null;
			ChatService catService = ServiceManager.getManager().getChatService();
			switch (sendMessage.getChannelId()) {
				case Common.CHAT_WORLD :
					channel = "世界";
					catService.sendMessageToWorld(receiveMessage, player);
					break;
				case Common.CHAT_GUILD :
					channel = "公会";
					catService.sendMessageToGuild(receiveMessage, player);
					break;
				case Common.CHAT_WHISPER :
					channel = "私聊";
					catService.sendMessageToPlayer(receiveMessage, player, sendMessage.getPlayerId());
					break;
			// case Common.CHAT_TEAM :
			// channel = "队伍";
			// catService.sendMessageToTeam(receiveMessage,
			// player);
			// break;
			}

			// ServiceManager.getManager().getChatService().addChatRecord(player.getName(),
			// sendMessage.getPlayerName(), sendMessage.getChannel(),
			// sendMessage.getMessage());//聊天缓存
			int receiveLevel = 0;
			if (receiveMessage.getReveId() > 0) {
				WorldPlayer worldPlayer = ServiceManager.getManager().getPlayerService().getPlayer(receiveMessage.getReveId());
				if (worldPlayer != null) {
					receiveLevel = worldPlayer.getPlayer().getId();
				}
			}
			GameLogService.chart(receiveMessage.getSendId(), player.getPlayer().getLv(), receiveMessage.getReveId(), receiveLevel, channel, sendMessage.getChannelId(), sendMessage.getMessage());
		} catch (ProtocolException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error(ex, ex);
		}
		return null;
	}
}
