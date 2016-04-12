package com.app.empire.world.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.dao.mongo.impl.PlayerHeroDao;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.entity.mongo.Skill;
import com.app.empire.world.entity.mysql.gameConfig.BaseLanguage;
import com.app.empire.world.entity.mysql.gameConfig.Hero;
import com.app.empire.world.entity.mysql.gameConfig.HeroExt;
import com.app.empire.world.entity.mysql.gameConfig.SkillExt;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.protocol.exception.ProtocolException;

@Service
@SuppressWarnings("unused")
public class PlayerSkillService {
	private Logger log = Logger.getLogger(PlayerSkillService.class);
	@Autowired
	private PlayerHeroDao heroDao;
	/**
	 * 天赋技能学习/升级
	 * 
	 * @param worldPlayer
	 * @param heroId 英雄流水id
	 * @param skillBaseId
	 * @return
	 * @throws PlayerDataException
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Skill studySkill(WorldPlayer worldPlayer, int heroId, int skillBaseId) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		PlayerHero playerHero = ServiceManager.getManager().getPlayerHeroService().getHeroByHeroId(worldPlayer, heroId);
		if (playerHero == null) {
			throw new PlayerDataException(gameConfigService.getMsg(10));// 英雄不存在
		}
		Map<Integer, Map> skillExt = gameConfigService.getGameConfig().get(SkillExt.class.getSimpleName());
		Map<Integer, Map> hero = gameConfigService.getGameConfig().get(Hero.class.getSimpleName());
		Map<Integer, Map> heroExt = gameConfigService.getGameConfig().get(HeroExt.class.getSimpleName());
		int heroBaseId = Integer.parseInt(heroExt.get(playerHero.getHeroExtId()).get("heroBaseId").toString());

		String talentSkill = hero.get(heroBaseId).get("talentSkill").toString();// 英雄天赋技能配置
		Map<String, String> talentSkills = CommonUtil.strToMap2(talentSkill);
		List<Integer> talentSkillIds = new ArrayList<Integer>();// 英雄初始天赋技能学习
		for (Entry<String, String> entry : talentSkills.entrySet()) {
			talentSkillIds.add(Integer.parseInt(entry.getValue()));
		}
		Map<Integer, Skill> skills = playerHero.getSkill();// 已经拥有的技能
		if (!talentSkillIds.contains(skillBaseId))
			throw new PlayerDataException(gameConfigService.getMsg(4));// 不能学习此技能
		Skill skill = skills == null ? null : skills.get(skillBaseId);// 自己已经有的技能
		Map skillNext = null;
		if (skill == null) {// 此技能没有学习
			for (Map skillExtEntry : skillExt.values()) {// 寻找lv=1
				int baseId = (int) skillExtEntry.get("baseId");
				int lv = (int) skillExtEntry.get("lv");
				if (baseId == skillBaseId && lv == 1) {
					skillNext = skillExtEntry;
					break;
				}
			}
		} else {// 学习了此技能，寻找下个等级技能
			int skillExtId = skill.getSkillExtId();
			int nextSkillId = (int) skillExt.get(skillExtId).get("nextSkillId");
			skillNext = skillExt.get(nextSkillId);
			if (nextSkillId <= 0 || skillNext == null)
				throw new PlayerDataException(gameConfigService.getMsg(5));// 已经是最高级
		}
		// 判断学习条件
		String conditionStr = skillNext.get("condition").toString();
		if (conditionStr != null & !conditionStr.equals("")) {
			List condition = Arrays.asList(conditionStr.split(":"));
			int needHeroLv = Integer.parseInt(condition.get(0).toString());// 英雄需要达到等级
			int pevSkillId = Integer.parseInt(condition.get(1).toString());// 前置技能基表id
			int pevSkillLV = Integer.parseInt(condition.get(2).toString());// 前置技能需要等级
			int needUse = Integer.parseInt(condition.get(3).toString());// 需要达到消耗天赋值
			if (playerHero.getLv() < needHeroLv)
				throw new PlayerDataException(gameConfigService.getMsg(6));// 英雄等级不足
			if (pevSkillId > 0) {
				Skill mySkill = skills == null ? null : skills.get(pevSkillId);
				if (mySkill == null || mySkill.getLv() < pevSkillLV)
					throw new PlayerDataException(gameConfigService.getMsg(7));// 前置技能等级不足
			}
			if (playerHero.getUseTalent() < needUse)
				throw new PlayerDataException(gameConfigService.getMsg(8));// 天赋值没有达到使用数量
		}
		int needTalentValue = Integer.parseInt(skillNext.get("needTalentValue").toString());// 需要天赋数
		ServiceManager.getManager().getPlayerHeroService().useTalent(worldPlayer, playerHero, -needTalentValue);// 使用天赋值
		if (skill == null) {
			skill = new Skill();
			skill.setSkillBaseId(skillBaseId);
			if (skills == null) {
				skills = new HashMap<Integer, Skill>();
				playerHero.setSkill(skills);
			}
			skills.put(skillBaseId, skill);
		}
		skill.setSkillExtId(Integer.parseInt(skillNext.get("id").toString()));
		HashMap property = new HashMap();
		property.put("aa", Arrays.asList(skillNext.get("aa").toString()));
		property.put("ab", Arrays.asList(skillNext.get("ab").toString()));
		property.put("ac", Arrays.asList(skillNext.get("ac").toString()));
		property.put("ad", Arrays.asList(skillNext.get("ad").toString()));
		property.put("ae", Arrays.asList(skillNext.get("ae").toString()));
		property.put("af", Arrays.asList(skillNext.get("af").toString()));
		property.put("ag", Arrays.asList(skillNext.get("ag").toString()));
		property.put("ah", Arrays.asList(skillNext.get("ah").toString()));
		property.put("ai", Arrays.asList(skillNext.get("ai").toString()));
		property.put("aj", Arrays.asList(skillNext.get("aj").toString()));
		property.put("ak", Arrays.asList(skillNext.get("ak").toString()));
		property.put("al", Arrays.asList(skillNext.get("al").toString()));
		property.put("am", Arrays.asList(skillNext.get("am").toString()));
		property.put("an", Arrays.asList(skillNext.get("an").toString()));
		property.put("ao", Arrays.asList(skillNext.get("ao").toString()));
		property.put("ap", Arrays.asList(skillNext.get("ap").toString()));
		property.put("aq", Arrays.asList(skillNext.get("aq").toString()));
		property.put("ar", Arrays.asList(skillNext.get("ar").toString()));
		skill.setProperty(CommonUtil.mapToStr(property));
		skill.setLv(Integer.parseInt(skillNext.get("lv").toString()));
		this.heroDao.save(playerHero);
		return skill;
	}

	/**
	 * 获取英雄技能
	 * 
	 * @param worldPlayer
	 * @param heroId
	 * @return Map<playerId, List<Skill>>
	 */
	public Map<Integer, List<Skill>> getHeroSkill(WorldPlayer worldPlayer, List<Integer> heroId) {
		List<PlayerHero> hero = ServiceManager.getManager().getPlayerHeroService().getHeroListByPlayerIdAndHeroId(worldPlayer, heroId);
		Map<Integer, List<Skill>> skills = new HashMap<Integer, List<Skill>>();
		for (PlayerHero playerHero : hero) {
			int hId = playerHero.getId();
			Map<Integer, Skill> skill = playerHero.getSkill();
			if (skill != null) {
				List<Skill> skillList = new ArrayList<Skill>();
				for (Skill entry : skill.values()) {
					skillList.add(entry);
				}
				skills.put(hId, skillList);
			}
		}
		return skills;
	}

	/**
	 * 重置英雄天赋技能
	 * 
	 * @param worldPlayer
	 */
	public PlayerHero resetHeroSkill(WorldPlayer worldPlayer, int heroId) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		int setScoreTotal = Integer.parseInt(gameConfigService.getPar(4)); // 需要的钻石
		boolean run = ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "diamond", -setScoreTotal);
		if (!run)
			throw new PlayerDataException(gameConfigService.getMsg(10));// 钻石不足

		PlayerHero playerHero = ServiceManager.getManager().getPlayerHeroService().getHeroByHeroId(worldPlayer, heroId);
		if (playerHero == null)
			throw new PlayerDataException(gameConfigService.getMsg(10));// 英雄不存在

		int useTalent = playerHero.getUseTalent();// 已经使用了的天赋数
		ServiceManager.getManager().getPlayerHeroService().useTalent(worldPlayer, playerHero, useTalent);// 加天赋
		playerHero.setUseTalent(0);
		playerHero.getSkill().clear();// 清空天赋
		this.heroDao.save(playerHero);
		return playerHero;
	}

}
