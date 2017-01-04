package com.app.db.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SkillProperty entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "skill_property", catalog = "game_config")
public class SkillProperty implements java.io.Serializable {

	// Fields

	private Integer templateId;
	private Integer type;
	private Integer soul;
	private Integer blood;
	private Integer attack;
	private Integer defence;
	private Integer soulAttack;
	private Integer soulDefence;
	private Integer accurate;
	private Integer dodge;
	private Integer crit;
	private Integer critDefence;
	private Integer critAddtion;
	private Integer critCut;
	private Integer attackAddtion;
	private Integer attackCut;
	private Integer soulAttackAddtion;
	private Integer soulAttackCut;
	private Integer regainSoul;
	private Integer regainBlood;
	private Integer metal;
	private Integer wood;
	private Integer water;
	private Integer fire;
	private Integer earth;
	private Integer metalDefence;
	private Integer woodDefence;
	private Integer waterDefence;
	private Integer fireDefence;
	private Integer earthDefence;
	private Integer speed;

	// Constructors

	/** default constructor */
	public SkillProperty() {
	}

	/** minimal constructor */
	public SkillProperty(Integer templateId) {
		this.templateId = templateId;
	}

	/** full constructor */
	public SkillProperty(Integer templateId, Integer type, Integer soul, Integer blood, Integer attack,
			Integer defence, Integer soulAttack, Integer soulDefence, Integer accurate, Integer dodge, Integer crit,
			Integer critDefence, Integer critAddtion, Integer critCut, Integer attackAddtion, Integer attackCut,
			Integer soulAttackAddtion, Integer soulAttackCut, Integer regainSoul, Integer regainBlood, Integer metal,
			Integer wood, Integer water, Integer fire, Integer earth, Integer metalDefence, Integer woodDefence,
			Integer waterDefence, Integer fireDefence, Integer earthDefence, Integer speed) {
		this.templateId = templateId;
		this.type = type;
		this.soul = soul;
		this.blood = blood;
		this.attack = attack;
		this.defence = defence;
		this.soulAttack = soulAttack;
		this.soulDefence = soulDefence;
		this.accurate = accurate;
		this.dodge = dodge;
		this.crit = crit;
		this.critDefence = critDefence;
		this.critAddtion = critAddtion;
		this.critCut = critCut;
		this.attackAddtion = attackAddtion;
		this.attackCut = attackCut;
		this.soulAttackAddtion = soulAttackAddtion;
		this.soulAttackCut = soulAttackCut;
		this.regainSoul = regainSoul;
		this.regainBlood = regainBlood;
		this.metal = metal;
		this.wood = wood;
		this.water = water;
		this.fire = fire;
		this.earth = earth;
		this.metalDefence = metalDefence;
		this.woodDefence = woodDefence;
		this.waterDefence = waterDefence;
		this.fireDefence = fireDefence;
		this.earthDefence = earthDefence;
		this.speed = speed;
	}

	// Property accessors
	@Id
	@Column(name = "templateId", unique = true, nullable = false)
	public Integer getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "soul")
	public Integer getSoul() {
		return this.soul;
	}

	public void setSoul(Integer soul) {
		this.soul = soul;
	}

	@Column(name = "blood")
	public Integer getBlood() {
		return this.blood;
	}

	public void setBlood(Integer blood) {
		this.blood = blood;
	}

	@Column(name = "attack")
	public Integer getAttack() {
		return this.attack;
	}

	public void setAttack(Integer attack) {
		this.attack = attack;
	}

	@Column(name = "defence")
	public Integer getDefence() {
		return this.defence;
	}

	public void setDefence(Integer defence) {
		this.defence = defence;
	}

	@Column(name = "soulAttack")
	public Integer getSoulAttack() {
		return this.soulAttack;
	}

	public void setSoulAttack(Integer soulAttack) {
		this.soulAttack = soulAttack;
	}

	@Column(name = "soulDefence")
	public Integer getSoulDefence() {
		return this.soulDefence;
	}

	public void setSoulDefence(Integer soulDefence) {
		this.soulDefence = soulDefence;
	}

	@Column(name = "accurate")
	public Integer getAccurate() {
		return this.accurate;
	}

	public void setAccurate(Integer accurate) {
		this.accurate = accurate;
	}

	@Column(name = "dodge")
	public Integer getDodge() {
		return this.dodge;
	}

	public void setDodge(Integer dodge) {
		this.dodge = dodge;
	}

	@Column(name = "crit")
	public Integer getCrit() {
		return this.crit;
	}

	public void setCrit(Integer crit) {
		this.crit = crit;
	}

	@Column(name = "critDefence")
	public Integer getCritDefence() {
		return this.critDefence;
	}

	public void setCritDefence(Integer critDefence) {
		this.critDefence = critDefence;
	}

	@Column(name = "critAddtion")
	public Integer getCritAddtion() {
		return this.critAddtion;
	}

	public void setCritAddtion(Integer critAddtion) {
		this.critAddtion = critAddtion;
	}

	@Column(name = "critCut")
	public Integer getCritCut() {
		return this.critCut;
	}

	public void setCritCut(Integer critCut) {
		this.critCut = critCut;
	}

	@Column(name = "attackAddtion")
	public Integer getAttackAddtion() {
		return this.attackAddtion;
	}

	public void setAttackAddtion(Integer attackAddtion) {
		this.attackAddtion = attackAddtion;
	}

	@Column(name = "attackCut")
	public Integer getAttackCut() {
		return this.attackCut;
	}

	public void setAttackCut(Integer attackCut) {
		this.attackCut = attackCut;
	}

	@Column(name = "soulAttackAddtion")
	public Integer getSoulAttackAddtion() {
		return this.soulAttackAddtion;
	}

	public void setSoulAttackAddtion(Integer soulAttackAddtion) {
		this.soulAttackAddtion = soulAttackAddtion;
	}

	@Column(name = "soulAttackCut")
	public Integer getSoulAttackCut() {
		return this.soulAttackCut;
	}

	public void setSoulAttackCut(Integer soulAttackCut) {
		this.soulAttackCut = soulAttackCut;
	}

	@Column(name = "regainSoul")
	public Integer getRegainSoul() {
		return this.regainSoul;
	}

	public void setRegainSoul(Integer regainSoul) {
		this.regainSoul = regainSoul;
	}

	@Column(name = "regainBlood")
	public Integer getRegainBlood() {
		return this.regainBlood;
	}

	public void setRegainBlood(Integer regainBlood) {
		this.regainBlood = regainBlood;
	}

	@Column(name = "metal")
	public Integer getMetal() {
		return this.metal;
	}

	public void setMetal(Integer metal) {
		this.metal = metal;
	}

	@Column(name = "wood")
	public Integer getWood() {
		return this.wood;
	}

	public void setWood(Integer wood) {
		this.wood = wood;
	}

	@Column(name = "water")
	public Integer getWater() {
		return this.water;
	}

	public void setWater(Integer water) {
		this.water = water;
	}

	@Column(name = "fire")
	public Integer getFire() {
		return this.fire;
	}

	public void setFire(Integer fire) {
		this.fire = fire;
	}

	@Column(name = "earth")
	public Integer getEarth() {
		return this.earth;
	}

	public void setEarth(Integer earth) {
		this.earth = earth;
	}

	@Column(name = "metalDefence")
	public Integer getMetalDefence() {
		return this.metalDefence;
	}

	public void setMetalDefence(Integer metalDefence) {
		this.metalDefence = metalDefence;
	}

	@Column(name = "woodDefence")
	public Integer getWoodDefence() {
		return this.woodDefence;
	}

	public void setWoodDefence(Integer woodDefence) {
		this.woodDefence = woodDefence;
	}

	@Column(name = "waterDefence")
	public Integer getWaterDefence() {
		return this.waterDefence;
	}

	public void setWaterDefence(Integer waterDefence) {
		this.waterDefence = waterDefence;
	}

	@Column(name = "fireDefence")
	public Integer getFireDefence() {
		return this.fireDefence;
	}

	public void setFireDefence(Integer fireDefence) {
		this.fireDefence = fireDefence;
	}

	@Column(name = "earthDefence")
	public Integer getEarthDefence() {
		return this.earthDefence;
	}

	public void setEarthDefence(Integer earthDefence) {
		this.earthDefence = earthDefence;
	}

	@Column(name = "speed")
	public Integer getSpeed() {
		return this.speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

}