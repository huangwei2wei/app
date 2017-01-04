package com.app.empire.world.server.handler.skill;

import com.app.empire.protocol.data.hero.StudySkill;
import com.app.empire.protocol.data.hero.StudySkillOK;
import com.app.empire.world.entity.mongo.HeroSkill;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;
/***
 * 技能学习
 * 
 * @author doter
 * 
 */
public class StudySkillHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		StudySkill studySkill = (StudySkill) data;
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		int heroId = studySkill.getHeroId();// 要学习升级的玩家英雄id
		int skillExtId = studySkill.getSkillBaseId();// 要学习升级的技能扩展id
		try {
			HeroSkill skill = ServiceManager.getManager().getPlayerSkillService().studySkill(worldPlayer, heroId, skillExtId);
			StudySkillOK studySkillOK = new StudySkillOK(studySkill.getSessionId(), studySkill.getSerial());
			studySkillOK.setHeroId(heroId);
			studySkillOK.setSkillBaseId(skill.getSkillBaseId());
			studySkillOK.setSkillExtId(skill.getSkillExtId());
			studySkillOK.setLv(skill.getLv());
			studySkillOK.setProperty(skill.getProperty());
			return studySkillOK;
		} catch (PlayerDataException ex) {
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}
