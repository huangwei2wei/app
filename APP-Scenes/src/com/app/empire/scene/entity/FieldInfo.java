package com.app.empire.scene.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * FieldInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "field_info", catalog = "game_config")
public class FieldInfo implements java.io.Serializable {

	// Fields

	private Integer mapKey;
	private String name;
	private Short type;
	private String desc;
	private Integer campaignId;
	private Integer campaignIndex;
	private Integer mapId;
	private String preMapKey;
	private Integer x;
	private Integer y;
	private Integer z;
	private Integer initScriptId;
	private String resName;
	private Boolean isBattle;
	private String startBattleTime;
	private String endBattleTime;
	private String startTime;
	private String endTime;

	// Constructors

	/** default constructor */
	public FieldInfo() {
	}

	/** minimal constructor */
	public FieldInfo(Integer mapKey) {
		this.mapKey = mapKey;
	}

	/** full constructor */
	public FieldInfo(Integer mapKey, String name, Short type, String desc, Integer campaignId, Integer campaignIndex,
			Integer mapId, String preMapKey, Integer x, Integer y, Integer z, Integer initScriptId, String resName,
			Boolean isBattle, String startBattleTime, String endBattleTime, String startTime, String endTime) {
		this.mapKey = mapKey;
		this.name = name;
		this.type = type;
		this.desc = desc;
		this.campaignId = campaignId;
		this.campaignIndex = campaignIndex;
		this.mapId = mapId;
		this.preMapKey = preMapKey;
		this.x = x;
		this.y = y;
		this.z = z;
		this.initScriptId = initScriptId;
		this.resName = resName;
		this.isBattle = isBattle;
		this.startBattleTime = startBattleTime;
		this.endBattleTime = endBattleTime;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	// Property accessors
	@Id
	@Column(name = "mapKey", unique = true, nullable = false)
	public Integer getMapKey() {
		return this.mapKey;
	}

	public void setMapKey(Integer mapKey) {
		this.mapKey = mapKey;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type")
	public Short getType() {
		return this.type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	@Column(name = "_desc")
	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name = "campaignId")
	public Integer getCampaignId() {
		return this.campaignId;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

	@Column(name = "campaignIndex")
	public Integer getCampaignIndex() {
		return this.campaignIndex;
	}

	public void setCampaignIndex(Integer campaignIndex) {
		this.campaignIndex = campaignIndex;
	}

	@Column(name = "mapId")
	public Integer getMapId() {
		return this.mapId;
	}

	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	@Column(name = "preMapKey")
	public String getPreMapKey() {
		return this.preMapKey;
	}

	public void setPreMapKey(String preMapKey) {
		this.preMapKey = preMapKey;
	}

	@Column(name = "x")
	public Integer getX() {
		return this.x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	@Column(name = "y")
	public Integer getY() {
		return this.y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	@Column(name = "z")
	public Integer getZ() {
		return this.z;
	}

	public void setZ(Integer z) {
		this.z = z;
	}

	@Column(name = "initScriptId")
	public Integer getInitScriptId() {
		return this.initScriptId;
	}

	public void setInitScriptId(Integer initScriptId) {
		this.initScriptId = initScriptId;
	}

	@Column(name = "resName")
	public String getResName() {
		return this.resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	@Column(name = "isBattle")
	public Boolean getIsBattle() {
		return this.isBattle;
	}

	public void setIsBattle(Boolean isBattle) {
		this.isBattle = isBattle;
	}

	@Column(name = "startBattleTime")
	public String getStartBattleTime() {
		return this.startBattleTime;
	}

	public void setStartBattleTime(String startBattleTime) {
		this.startBattleTime = startBattleTime;
	}

	@Column(name = "endBattleTime")
	public String getEndBattleTime() {
		return this.endBattleTime;
	}

	public void setEndBattleTime(String endBattleTime) {
		this.endBattleTime = endBattleTime;
	}

	@Column(name = "startTime")
	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "endTime")
	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}