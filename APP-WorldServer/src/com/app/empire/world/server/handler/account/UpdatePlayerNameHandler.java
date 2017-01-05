package com.app.empire.world.server.handler.account;

import com.app.empire.protocol.data.account.UpdatePlayerName;
import com.app.empire.protocol.data.account.UpdatePlayerNameOk;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 修改玩家角色昵称
 * 
 * @author doter
 * 
 */
public class UpdatePlayerNameHandler implements IDataHandler {
	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		UpdatePlayerName updatePlayerName = (UpdatePlayerName) data;
		String nickname = updatePlayerName.getNickname();
		try {
			ServiceManager.getManager().getPlayerService().updateName(worldPlayer, nickname);
			UpdatePlayerNameOk ok = new UpdatePlayerNameOk(data.getSessionId(), data.getSerial());
			session.write(ok);
		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
