package com.app.db.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Skillinfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "skill_info", catalog = "game_config")
public class SkillInfo implements java.io.Serializable {

	// Fields

	private Integer templateId;
	private String templateName;
	private Integer icons;
	private Integer masterType;
	private Integer sonType;
	private Integer grandsonType;
	private Integer level;
	private String preTemplateId;
	private Integer nextTemplateId;
	private Integer needGrades;
	private Integer useWay;
	private Integer actionId;
	private Integer needStone;
	private Integer needRepair;
	private Integer needJade;
	private String needGoods;
	private String propertyIds;
	private String sysBufferIds;
	private String fightBufferIds;
	private String description;

	// Constructors

	/** default constructor */
	public SkillInfo() {
	}

	/** minimal constructor */
	public SkillInfo(Integer templateId) {
		this.templateId = templateId;
	}

	/** full constructor */
	public SkillInfo(Integer templateId, String templateName, Integer icons, Integer masterType, Integer sonType, Integer grandsonType, Integer level, String preTemplateId,
			Integer nextTemplateId, Integer needGrades, Integer useWay, Integer actionId, Integer needStone, Integer needRepair, Integer needJade, String needGoods,
			String propertyIds, String sysBufferIds, String fightBufferIds, String description) {
		this.templateId = templateId;
		this.templateName = templateName;
		this.icons = icons;
		this.masterType = masterType;
		this.sonType = sonType;
		this.grandsonType = grandsonType;
		this.level = level;
		this.preTemplateId = preTemplateId;
		this.nextTemplateId = nextTemplateId;
		this.needGrades = needGrades;
		this.useWay = useWay;
		this.actionId = actionId;
		this.needStone = needStone;
		this.needRepair = needRepair;
		this.needJade = needJade;
		this.needGoods = needGoods;
		this.propertyIds = propertyIds;
		this.sysBufferIds = sysBufferIds;
		this.fightBufferIds = fightBufferIds;
		this.description = description;
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

	@Column(name = "templateName")
	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Column(name = "icons")
	public Integer getIcons() {
		return this.icons;
	}

	public void setIcons(Integer icons) {
		this.icons = icons;
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

	@Column(name = "grandsonType")
	public Integer getGrandsonType() {
		return this.grandsonType;
	}

	public void setGrandsonType(Integer grandsonType) {
		this.grandsonType = grandsonType;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "preTemplateId")
	public String getPreTemplateId() {
		return this.preTemplateId;
	}

	public void setPreTemplateId(String preTemplateId) {
		this.preTemplateId = preTemplateId;
	}

	@Column(name = "nextTemplateId")
	public Integer getNextTemplateId() {
		return this.nextTemplateId;
	}

	public void setNextTemplateId(Integer nextTemplateId) {
		this.nextTemplateId = nextTemplateId;
	}

	@Column(name = "needGrades")
	public Integer getNeedGrades() {
		return this.needGrades;
	}

	public void setNeedGrades(Integer needGrades) {
		this.needGrades = needGrades;
	}

	@Column(name = "useWay")
	public Integer getUseWay() {
		return this.useWay;
	}

	public void setUseWay(Integer useWay) {
		this.useWay = useWay;
	}

	@Column(name = "actionId")
	public Integer getActionId() {
		return this.actionId;
	}

	public void setActionId(Integer actionId) {
		this.actionId = actionId;
	}

	@Column(name = "needStone")
	public Integer getNeedStone() {
		return this.needStone;
	}

	public void setNeedStone(Integer needStone) {
		this.needStone = needStone;
	}

	@Column(name = "needRepair")
	public Integer getNeedRepair() {
		return this.needRepair;
	}

	public void setNeedRepair(Integer needRepair) {
		this.needRepair = needRepair;
	}

	@Column(name = "needJade")
	public Integer getNeedJade() {
		return this.needJade;
	}

	public void setNeedJade(Integer needJade) {
		this.needJade = needJade;
	}

	@Column(name = "needGoods")
	public String getNeedGoods() {
		return this.needGoods;
	}

	public void setNeedGoods(String needGoods) {
		this.needGoods = needGoods;
	}

	@Column(name = "propertyIds")
	public String getPropertyIds() {
		return this.propertyIds;
	}

	public void setPropertyIds(String propertyIds) {
		this.propertyIds = propertyIds;
	}

	@Column(name = "sysBufferIds")
	public String getSysBufferIds() {
		return this.sysBufferIds;
	}

	public void setSysBufferIds(String sysBufferIds) {
		this.sysBufferIds = sysBufferIds;
	}

	@Column(name = "fightBufferIds")
	public String getFightBufferIds() {
		return this.fightBufferIds;
	}

	public void setFightBufferIds(String fightBufferIds) {
		this.fightBufferIds = fightBufferIds;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}