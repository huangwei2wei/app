package com.app.empire.world.entity.mongo;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.app.db.mongo.entity.IEntity;

/**
 * 英雄表
 * 
 * @author doter
 */

@Document(collection = "player_hero")
public class PlayerHero extends IEntity {
	private int playerId;// 用户id
	private int heroBaseId;// 基表英雄id
	private int heroExtId;// 英雄扩展id
	private int experience;// 经验
	private int lv;// 等级
	private Map<Integer, Skill> skill;// 技能 baseId->Skill
	private int fight;// 战斗力
	private String property;// 所有属性汇总
	private String equipPro;// 装备属性汇总
	private int hp;
	private int talent;// 天赋数
	private int useTalent;// 使用了多少天赋值

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getHeroBaseId() {
		return heroBaseId;
	}
	public void setHeroBaseId(int heroBaseId) {
		this.heroBaseId = heroBaseId;
	}
	public int getHeroExtId() {
		return heroExtId;
	}
	public void setHeroExtId(int heroExtId) {
		this.heroExtId = heroExtId;
	}
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	public int getLv() {
		return lv;
	}
	public void setLv(int lv) {
		this.lv = lv;
	}
	public Map<Integer, Skill> getSkill() {
		return skill;
	}
	public void setSkill(Map<Integer, Skill> skill) {
		this.skill = skill;
	}
	public int getFight() {
		return fight;
	}
	public void setFight(int fight) {
		this.fight = fight;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getEquipPro() {
		return equipPro;
	}
	public void setEquipPro(String equipPro) {
		this.equipPro = equipPro;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int getTalent() {
		return talent;
	}
	public void setTalent(int talent) {
		this.talent = talent;
	}
	public int getUseTalent() {
		return useTalent;
	}
	public void setUseTalent(int useTalent) {
		this.useTalent = useTalent;
	}

}
