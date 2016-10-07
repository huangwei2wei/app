package com.app.empire.scene.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SkillActioninfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "skill_actioninfo", catalog = "game_config")
public class SkillActioninfo implements java.io.Serializable {

	// Fields

	private Integer templateId;
	private Integer masterType;
	private Integer sonType;
	private Integer combo;
	private Integer maxCombo;
	private Integer animation;
	private Integer audio;
	private Integer search;
	private Integer effect;
	private Integer move;
	private Integer camera;
	private Integer attackTimes;
	private Integer costType;
	private Integer costCount;
	private Integer attackType;
	private Integer paramValue1;
	private Integer paramValue2;
	private Integer paramValue3;
	private Integer paramParent1;
	private Integer paramParent2;
	private Integer paramParent3;
	private String bufferIds;
	private String trapIds;
	private Integer random;
	private Integer isCrit;
	private Integer priority;
	private Integer costTime;
	private Integer cooldown;

	// Constructors

	/** default constructor */
	public SkillActioninfo() {
	}

	/** minimal constructor */
	public SkillActioninfo(Integer templateId) {
		this.templateId = templateId;
	}

	/** full constructor */
	public SkillActioninfo(Integer templateId, Integer masterType, Integer sonType, Integer combo, Integer maxCombo,
			Integer animation, Integer audio, Integer search, Integer effect, Integer move, Integer camera,
			Integer attackTimes, Integer costType, Integer costCount, Integer attackType, Integer paramValue1,
			Integer paramValue2, Integer paramValue3, Integer paramParent1, Integer paramParent2, Integer paramParent3,
			String bufferIds, String trapIds, Integer random, Integer isCrit, Integer priority, Integer costTime,
			Integer cooldown) {
		this.templateId = templateId;
		this.masterType = masterType;
		this.sonType = sonType;
		this.combo = combo;
		this.maxCombo = maxCombo;
		this.animation = animation;
		this.audio = audio;
		this.search = search;
		this.effect = effect;
		this.move = move;
		this.camera = camera;
		this.attackTimes = attackTimes;
		this.costType = costType;
		this.costCount = costCount;
		this.attackType = attackType;
		this.paramValue1 = paramValue1;
		this.paramValue2 = paramValue2;
		this.paramValue3 = paramValue3;
		this.paramParent1 = paramParent1;
		this.paramParent2 = paramParent2;
		this.paramParent3 = paramParent3;
		this.bufferIds = bufferIds;
		this.trapIds = trapIds;
		this.random = random;
		this.isCrit = isCrit;
		this.priority = priority;
		this.costTime = costTime;
		this.cooldown = cooldown;
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

	@Column(name = "masterType")
	public Integer getMasterType() {
		return this.masterType;
	}

	public void setMasterType(Integer masterType) {
		this.masterType = masterType;
	}

	@Column(name = "sonType")
	public Integer getSonType() {
		return this.sonType;
	}

	public void setSonType(Integer sonType) {
		this.sonType = sonType;
	}

	@Column(name = "combo")
	public Integer getCombo() {
		return this.combo;
	}

	public void setCombo(Integer combo) {
		this.combo = combo;
	}

	@Column(name = "maxCombo")
	public Integer getMaxCombo() {
		return this.maxCombo;
	}

	public void setMaxCombo(Integer maxCombo) {
		this.maxCombo = maxCombo;
	}

	@Column(name = "animation")
	public Integer getAnimation() {
		return this.animation;
	}

	public void setAnimation(Integer animation) {
		this.animation = animation;
	}

	@Column(name = "audio")
	public Integer getAudio() {
		return this.audio;
	}

	public void setAudio(Integer audio) {
		this.audio = audio;
	}

	@Column(name = "search")
	public Integer getSearch() {
		return this.search;
	}

	public void setSearch(Integer search) {
		this.search = search;
	}

	@Column(name = "effect")
	public Integer getEffect() {
		return this.effect;
	}

	public void setEffect(Integer effect) {
		this.effect = effect;
	}

	@Column(name = "move")
	public Integer getMove() {
		return this.move;
	}

	public void setMove(Integer move) {
		this.move = move;
	}

	@Column(name = "camera")
	public Integer getCamera() {
		return this.camera;
	}

	public void setCamera(Integer camera) {
		this.camera = camera;
	}

	@Column(name = "attackTimes")
	public Integer getAttackTimes() {
		return this.attackTimes;
	}

	public void setAttackTimes(Integer attackTimes) {
		this.attackTimes = attackTimes;
	}

	@Column(name = "costType")
	public Integer getCostType() {
		return this.costType;
	}

	public void setCostType(Integer costType) {
		this.costType = costType;
	}

	@Column(name = "costCount")
	public Integer getCostCount() {
		return this.costCount;
	}

	public void setCostCount(Integer costCount) {
		this.costCount = costCount;
	}

	@Column(name = "attackType")
	public Integer getAttackType() {
		return this.attackType;
	}

	public void setAttackType(Integer attackType) {
		this.attackType = attackType;
	}

	@Column(name = "paramValue1")
	public Integer getParamValue1() {
		return this.paramValue1;
	}

	public void setParamValue1(Integer paramValue1) {
		this.paramValue1 = paramValue1;
	}

	@Column(name = "paramValue2")
	public Integer getParamValue2() {
		return this.paramValue2;
	}

	public void setParamValue2(Integer paramValue2) {
		this.paramValue2 = paramValue2;
	}

	@Column(name = "paramValue3")
	public Integer getParamValue3() {
		return this.paramValue3;
	}

	public void setParamValue3(Integer paramValue3) {
		this.paramValue3 = paramValue3;
	}

	@Column(name = "paramParent1")
	public Integer getParamParent1() {
		return this.paramParent1;
	}

	public void setParamParent1(Integer paramParent1) {
		this.paramParent1 = paramParent1;
	}

	@Column(name = "paramParent2")
	public Integer getParamParent2() {
		return this.paramParent2;
	}

	public void setParamParent2(Integer paramParent2) {
		this.paramParent2 = paramParent2;
	}

	@Column(name = "paramParent3")
	public Integer getParamParent3() {
		return this.paramParent3;
	}

	public void setParamParent3(Integer paramParent3) {
		this.paramParent3 = paramParent3;
	}

	@Column(name = "bufferIds")
	public String getBufferIds() {
		return this.bufferIds;
	}

	public void setBufferIds(String bufferIds) {
		this.bufferIds = bufferIds;
	}

	@Column(name = "trapIds")
	public String getTrapIds() {
		return this.trapIds;
	}

	public void setTrapIds(String trapIds) {
		this.trapIds = trapIds;
	}

	@Column(name = "random")
	public Integer getRandom() {
		return this.random;
	}

	public void setRandom(Integer random) {
		this.random = random;
	}

	@Column(name = "isCrit")
	public Integer getIsCrit() {
		return this.isCrit;
	}

	public void setIsCrit(Integer isCrit) {
		this.isCrit = isCrit;
	}

	@Column(name = "priority")
	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Column(name = "costTime")
	public Integer getCostTime() {
		return this.costTime;
	}

	public void setCostTime(Integer costTime) {
		this.costTime = costTime;
	}

	@Column(name = "cooldown")
	public Integer getCooldown() {
		return this.cooldown;
	}

	public void setCooldown(Integer cooldown) {
		this.cooldown = cooldown;
	}

}