package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hero entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "hero", catalog = "game_config")
public class Hero implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Integer heroType;
	private String skill;
	private String talentSkill;
	private String modelName;
	private String icon;
	private String skillShow;
	private String talentShow;
	private Integer deathEffectsId;
	private Integer isCityShow;
	private Float att1;
	private Float att2;
	private Float att3;
	private String walkingSpeed;
	private String runningSpeed;
	private String parrySpeed;
	private Integer runningPower;
	private Float parryPower;
	private String introduce;

	// Constructors

	/** default constructor */
	public Hero() {
	}

	/** full constructor */
	public Hero(String name, Integer heroType, String skill, String talentSkill, String modelName, String icon, String skillShow, String talentShow, Integer deathEffectsId, Integer isCityShow,
			Float att1, Float att2, Float att3, String walkingSpeed, String runningSpeed, String parrySpeed, Integer runningPower, Float parryPower, String introduce) {
		this.name = name;
		this.heroType = heroType;
		this.skill = skill;
		this.talentSkill = talentSkill;
		this.modelName = modelName;
		this.icon = icon;
		this.skillShow = skillShow;
		this.talentShow = talentShow;
		this.deathEffectsId = deathEffectsId;
		this.isCityShow = isCityShow;
		this.att1 = att1;
		this.att2 = att2;
		this.att3 = att3;
		this.walkingSpeed = walkingSpeed;
		this.runningSpeed = runningSpeed;
		this.parrySpeed = parrySpeed;
		this.runningPower = runningPower;
		this.parryPower = parryPower;
		this.introduce = introduce;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false, length = 30)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "hero_type", nullable = false)
	public Integer getHeroType() {
		return this.heroType;
	}

	public void setHeroType(Integer heroType) {
		this.heroType = heroType;
	}

	@Column(name = "skill", nullable = false, length = 65535)
	public String getSkill() {
		return this.skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	@Column(name = "talent_skill", nullable = false, length = 65535)
	public String getTalentSkill() {
		return this.talentSkill;
	}

	public void setTalentSkill(String talentSkill) {
		this.talentSkill = talentSkill;
	}

	@Column(name = "model_name", nullable = false, length = 65535)
	public String getModelName() {
		return this.modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	@Column(name = "icon", nullable = false, length = 65535)
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Column(name = "skill_show", nullable = false, length = 65535)
	public String getSkillShow() {
		return this.skillShow;
	}

	public void setSkillShow(String skillShow) {
		this.skillShow = skillShow;
	}

	@Column(name = "talent_show", nullable = false, length = 65535)
	public String getTalentShow() {
		return this.talentShow;
	}

	public void setTalentShow(String talentShow) {
		this.talentShow = talentShow;
	}

	@Column(name = "death_effects_id", nullable = false)
	public Integer getDeathEffectsId() {
		return this.deathEffectsId;
	}

	public void setDeathEffectsId(Integer deathEffectsId) {
		this.deathEffectsId = deathEffectsId;
	}

	@Column(name = "is_city_show", nullable = false)
	public Integer getIsCityShow() {
		return this.isCityShow;
	}

	public void setIsCityShow(Integer isCityShow) {
		this.isCityShow = isCityShow;
	}

	@Column(name = "att_1", nullable = false, precision = 12, scale = 0)
	public Float getAtt1() {
		return this.att1;
	}

	public void setAtt1(Float att1) {
		this.att1 = att1;
	}

	@Column(name = "att_2", nullable = false, precision = 12, scale = 0)
	public Float getAtt2() {
		return this.att2;
	}

	public void setAtt2(Float att2) {
		this.att2 = att2;
	}

	@Column(name = "att_3", nullable = false, precision = 12, scale = 0)
	public Float getAtt3() {
		return this.att3;
	}

	public void setAtt3(Float att3) {
		this.att3 = att3;
	}

	@Column(name = "walking_speed", nullable = false, length = 65535)
	public String getWalkingSpeed() {
		return this.walkingSpeed;
	}

	public void setWalkingSpeed(String walkingSpeed) {
		this.walkingSpeed = walkingSpeed;
	}

	@Column(name = "running_speed", nullable = false, length = 65535)
	public String getRunningSpeed() {
		return this.runningSpeed;
	}

	public void setRunningSpeed(String runningSpeed) {
		this.runningSpeed = runningSpeed;
	}

	@Column(name = "parry_speed", nullable = false, length = 65535)
	public String getParrySpeed() {
		return this.parrySpeed;
	}

	public void setParrySpeed(String parrySpeed) {
		this.parrySpeed = parrySpeed;
	}

	@Column(name = "running_power", nullable = false)
	public Integer getRunningPower() {
		return this.runningPower;
	}

	public void setRunningPower(Integer runningPower) {
		this.runningPower = runningPower;
	}

	@Column(name = "parry_power", nullable = false, precision = 12, scale = 0)
	public Float getParryPower() {
		return this.parryPower;
	}

	public void setParryPower(Float parryPower) {
		this.parryPower = parryPower;
	}

	@Column(name = "introduce", nullable = false, length = 256)
	public String getIntroduce() {
		return this.introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

}