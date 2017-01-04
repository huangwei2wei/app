package com.app.empire.world.service.skill.manager;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.entity.mongo.HeroSkill;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;

@Service
@SuppressWarnings("unused")
public class PlayerSkillMgr {
	private Logger log = Logger.getLogger(PlayerSkillMgr.class);

	
	/**
	 * 天赋技能学习/升级
	 * 
	 * @param worldPlayer
	 * @param heroId 英雄流水id
	 * @param skillBaseId
	 * @return
	 * @throws PlayerDataException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HeroSkill studySkill(WorldPlayer worldPlayer, int heroId, int skillBaseId) throws PlayerDataException {
		return null;
	}

	/**
	 * 获取英雄技能
	 * 
	 * @param worldPlayer
	 * @param heroId
	 * @return Map<playerId, List<Skill>>
	 */
	public Map<Integer, List<HeroSkill>> getHeroSkill(WorldPlayer worldPlayer, List<Integer> heroId) {
		return null;
	}
}
