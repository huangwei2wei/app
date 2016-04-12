package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ArmyStars entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "army_stars", catalog = "game_config")
public class ArmyStars implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer armyType;
	private String resourceName;
	private Integer armyStar;
	private Float proAdd;
	private String openSkill;
	private Integer openSkillSign;
	private String consume;
	private String info;

	// Constructors

	/** default constructor */
	public ArmyStars() {
	}

	/** full constructor */
	public ArmyStars(Integer id, Integer armyType, String resourceName, Integer armyStar, Float proAdd, String openSkill, Integer openSkillSign, String consume, String info) {
		this.id = id;
		this.armyType = armyType;
		this.resourceName = resourceName;
		this.armyStar = armyStar;
		this.proAdd = proAdd;
		this.openSkill = openSkill;
		this.openSkillSign = openSkillSign;
		this.consume = consume;
		this.info = info;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "army_type", nullable = false)
	public Integer getArmyType() {
		return this.armyType;
	}

	public void setArmyType(Integer armyType) {
		this.armyType = armyType;
	}

	@Column(name = "resource_name", nullable = false, length = 500)
	public String getResourceName() {
		return this.resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	@Column(name = "army_star", nullable = false)
	public Integer getArmyStar() {
		return this.armyStar;
	}

	public void setArmyStar(Integer armyStar) {
		this.armyStar = armyStar;
	}

	@Column(name = "pro_add", nullable = false, precision = 12, scale = 0)
	public Float getProAdd() {
		return this.proAdd;
	}

	public void setProAdd(Float proAdd) {
		this.proAdd = proAdd;
	}

	@Column(name = "open_skill", nullable = false, length = 65535)
	public String getOpenSkill() {
		return this.openSkill;
	}

	public void setOpenSkill(String openSkill) {
		this.openSkill = openSkill;
	}

	@Column(name = "open_skill_sign", nullable = false)
	public Integer getOpenSkillSign() {
		return this.openSkillSign;
	}

	public void setOpenSkillSign(Integer openSkillSign) {
		this.openSkillSign = openSkillSign;
	}

	@Column(name = "consume", nullable = false, length = 500)
	public String getConsume() {
		return this.consume;
	}

	public void setConsume(String consume) {
		this.consume = consume;
	}

	@Column(name = "info", nullable = false, length = 65535)
	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}