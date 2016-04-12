package com.app.empire.world.server.handler.copymap;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.copymap.SaveTeam;
import com.app.empire.protocol.data.copymap.SaveTeamOk;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class SaveTeamHandler implements IDataHandler {
	private Logger log = Logger.getLogger(SaveTeamHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		SaveTeam saveTeam = (SaveTeam) data;
		ServiceManager.getManager().getPlayerTeamService().saveTeam(worldPlayer, saveTeam);

		SaveTeamOk saveTeamOk = new SaveTeamOk(data.getSessionId(), data.getSerial());
		return saveTeamOk;
	}
}
