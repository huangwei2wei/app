package com.app.empire.world.server.handler.hero;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.hero.ResetSkill;
import com.app.empire.protocol.data.hero.ResetSkillOK;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 重置英雄天赋技能
 * 
 * @author doter
 * 
 */
public class ResetSkillHandler implements IDataHandler {
	private Logger log = Logger.getLogger(ResetSkillHandler.class.getPackage().getName());
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		ResetSkill studySkill = (ResetSkill) data;
		int heroId = studySkill.getHeroId();
		try {
			PlayerHero playerHero = ServiceManager.getManager().getPlayerSkillService().resetHeroSkill(worldPlayer, heroId);
			ResetSkillOK ok = new ResetSkillOK(data.getSessionId(), data.getSerial());
			ok.setHeroId(playerHero.getId());
			ok.setTalent(playerHero.getTalent());
			return ok;
		} catch (PlayerDataException e) {
			log.info(e);
			throw new ProtocolException(e.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}

	}
}
