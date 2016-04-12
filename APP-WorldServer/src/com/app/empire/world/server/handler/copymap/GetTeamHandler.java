package com.app.empire.world.server.handler.copymap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.copymap.GetTeam;
import com.app.empire.protocol.data.copymap.GetTeamOk;
import com.app.empire.world.entity.mongo.PlayerTeam;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

public class GetTeamHandler implements IDataHandler {
	private Logger log = Logger.getLogger(AcessCopyMapHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		GetTeam getTeam = (GetTeam) data;
		int teamType = getTeam.getTeamType();
		PlayerTeam playerTeam = ServiceManager.getManager().getPlayerTeamService().getTeam(worldPlayer, teamType);
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		if (playerTeam != null)
			map = playerTeam.getArms();

		int heroId = playerTeam != null ? playerTeam.getHeroId() : 0;// 英雄id
		int[] arms = new int[map.size()];// 兵种
		int[] armsNum = new int[map.size()];// 兵种数量
		int animalId = playerTeam != null ? playerTeam.getAnimalId() : 0;// 神兽
		int i = 0;
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			arms[i] = entry.getKey();
			armsNum[i] = entry.getValue();
			i++;
		}

		GetTeamOk ok = new GetTeamOk(data.getSessionId(), data.getSerial());
		ok.setHeroId(heroId);
		ok.setArms(arms);
		ok.setArmsNum(armsNum);
		ok.setAnimalId(animalId);
		return ok;
	}
}
