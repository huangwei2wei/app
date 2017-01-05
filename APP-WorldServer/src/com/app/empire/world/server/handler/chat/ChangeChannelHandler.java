package com.app.empire.world.server.handler.chat;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.app.empire.protocol.data.chat.ChangeChannel;
import com.app.empire.world.common.util.Common;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 更换当前频道(暂时不使用)
 * 
 * @author doter
 */
public class ChangeChannelHandler implements IDataHandler {
	Logger log = Logger.getLogger(ChangeChannelHandler.class);

	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		ChangeChannel changeChannel = (ChangeChannel) data;
		WorldPlayer player = session.getPlayer(data.getSessionId());
		int channelId = changeChannel.getChannelId();
		ArrayList<String> channelIds = new ArrayList<String>();
		switch (channelId) {
			case Common.CHAT_WORLD :
				channelIds.add(Common.CHAT_GUILD_CHANNEL);
				break;
		}
		String[] addChannels = new String[channelIds.size()];
		channelIds.toArray(addChannels);
		ServiceManager.getManager().getChatService().syncChannels(session, player.getPlayer().getId(), addChannels, new String[0]);
	}
}
