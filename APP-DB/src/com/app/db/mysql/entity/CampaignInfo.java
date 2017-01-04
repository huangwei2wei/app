package com.app.db.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * CampaignInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "campaign_info", catalog = "game_config")
public class CampaignInfo implements java.io.Serializable {

	// Fields

	private Integer templateId;
	private String campaignName;
	private Integer storyId;
	private Integer type;
	private Integer difficulty;
	private Integer joinType;
	private Integer minLevel;
	private Integer maxLevel;
	private Integer preCampaignId;
	private String nextCampaignId;
	private Integer capacity;
	private String rewardItems;
	private String description;
	private Integer openType;
	private String openParams;
	private Integer openTime;
	private Integer startScriptId;
	private Integer endScriptId;
	private String taskIds;
	private String costItem;

	// Constructors

	/** default constructor */
	public CampaignInfo() {
	}

	/** minimal constructor */
	public CampaignInfo(Integer templateId) {
		this.templateId = templateId;
	}

	/** full constructor */
	public CampaignInfo(Integer templateId, String campaignName, Integer storyId, Integer type, Integer difficulty, Integer joinType, Integer minLevel, Integer maxLevel,
			Integer preCampaignId, String nextCampaignId, Integer capacity, String rewardItems, String description, Integer openType, String openParams, Integer openTime,
			Integer startScriptId, Integer endScriptId, String taskIds, String costItem) {
		this.templateId = templateId;
		this.campaignName = campaignName;
		this.storyId = storyId;
		this.type = type;
		this.difficulty = difficulty;
		this.joinType = joinType;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.preCampaignId = preCampaignId;
		this.nextCampaignId = nextCampaignId;
		this.capacity = capacity;
		this.rewardItems = rewardItems;
		this.description = description;
		this.openType = openType;
		this.openParams = openParams;
		this.openTime = openTime;
		this.startScriptId = startScriptId;
		this.endScriptId = endScriptId;
		this.taskIds = taskIds;
		this.costItem = costItem;
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

	@Column(name = "campaignName")
	public String getCampaignName() {
		return this.campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	@Column(name = "storyId")
	public Integer getStoryId() {
		return this.storyId;
	}

	public void setStoryId(Integer storyId) {
		this.storyId = storyId;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "difficulty")
	public Integer getDifficulty() {
		return this.difficulty;
	}

	public void setDifficulty(Integer difficulty) {
		this.difficulty = difficulty;
	}

	@Column(name = "joinType")
	public Integer getJoinType() {
		return this.joinType;
	}

	public void setJoinType(Integer joinType) {
		this.joinType = joinType;
	}

	@Column(name = "minLevel")
	public Integer getMinLevel() {
		return this.minLevel;
	}

	public void setMinLevel(Integer minLevel) {
		this.minLevel = minLevel;
	}

	@Column(name = "maxLevel")
	public Integer getMaxLevel() {
		return this.maxLevel;
	}

	public void setMaxLevel(Integer maxLevel) {
		this.maxLevel = maxLevel;
	}

	@Column(name = "preCampaignId")
	public Integer getPreCampaignId() {
		return this.preCampaignId;
	}

	public void setPreCampaignId(Integer preCampaignId) {
		this.preCampaignId = preCampaignId;
	}

	@Column(name = "nextCampaignId")
	public String getNextCampaignId() {
		return this.nextCampaignId;
	}

	public void setNextCampaignId(String nextCampaignId) {
		this.nextCampaignId = nextCampaignId;
	}

	@Column(name = "capacity")
	public Integer getCapacity() {
		return this.capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	@Column(name = "rewardItems", length = 655)
	public String getRewardItems() {
		return this.rewardItems;
	}

	public void setRewardItems(String rewardItems) {
		this.rewardItems = rewardItems;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "openType")
	public Integer getOpenType() {
		return this.openType;
	}

	public void setOpenType(Integer openType) {
		this.openType = openType;
	}

	@Column(name = "openParams")
	public String getOpenParams() {
		return this.openParams;
	}

	public void setOpenParams(String openParams) {
		this.openParams = openParams;
	}

	@Column(name = "openTime")
	public Integer getOpenTime() {
		return this.openTime;
	}

	public void setOpenTime(Integer openTime) {
		this.openTime = openTime;
	}

	@Column(name = "startScriptId")
	public Integer getStartScriptId() {
		return this.startScriptId;
	}

	public void setStartScriptId(Integer startScriptId) {
		this.startScriptId = startScriptId;
	}

	@Column(name = "endScriptId")
	public Integer getEndScriptId() {
		return this.endScriptId;
	}

	public void setEndScriptId(Integer endScriptId) {
		this.endScriptId = endScriptId;
	}

	@Column(name = "taskIds")
	public String getTaskIds() {
		return this.taskIds;
	}

	public void setTaskIds(String taskIds) {
		this.taskIds = taskIds;
	}

	@Column(name = "costItem")
	public String getCostItem() {
		return this.costItem;
	}

	public void setCostItem(String costItem) {
		this.costItem = costItem;
	}

}