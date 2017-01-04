package com.app.empire.world.service.skill;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.db.mongo.entity.Option;
import com.app.empire.world.dao.mongo.impl.HeroSkillDao;
import com.app.empire.world.entity.mongo.HeroSkill;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.IInventory;
import com.app.empire.world.service.factory.DaoManager;
import com.app.empire.world.service.factory.ServiceManager;

public class SkillInventory implements IInventory {
	private WorldPlayer worldPlayer;
	@Autowired
	private HeroSkillDao heroSkillDao;
	private Map<Integer, HeroSkill> heroSkills = new HashMap<Integer, HeroSkill>();

	public SkillInventory(WorldPlayer worldPlayer) {
		this.worldPlayer = worldPlayer;
	}

	@Override
	public boolean loadFromDataBase() {
		heroSkills = DaoManager.getManager().getHeroSkillDao().getHeroSkill(worldPlayer.getPlayer().getId());
		return true;
	}

	@Override
	public boolean unloadData() {
		heroSkills.clear();
		return true;
	}

	@Override
	public boolean saveToDatabase() {
		for (HeroSkill skill : heroSkills.values()) {
			if (skill.getOp() == Option.Insert) {
				heroSkillDao.insert(skill);
			} else if (skill.getOp() == Option.Update) {
				heroSkillDao.save(skill);
			}
		}
		return true;
	}

	public Map<Integer, HeroSkill> getSkill() {
		if (heroSkills.size() == 0) {
			loadFromDataBase();
		}
		return heroSkills;
	}

	public boolean addSkill(HeroSkill heroSkill) {
		if (heroSkills.containsKey(heroSkill.getSkillBaseId())) {
			heroSkill.setOp(Option.Update);
		} else {
			heroSkill.setOp(Option.Insert);
		}
		heroSkills.put(heroSkill.getSkillBaseId(), heroSkill);
		return true;
	}

}
