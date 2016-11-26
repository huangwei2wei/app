package com.app.empire.scene.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * FieldSpawn entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "field_spawn", catalog = "game_config")
public class FieldSpawn implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer mapid;
	private Integer boundX;
	private Integer boundY;
	private Integer boundZ;
	private Short initStatu;
	private Integer tagId;
	private String preSpawanId;
	private String nextSpawanId;
	private Integer wakeType;
	private Integer wakeDelay;
	private Integer toalCount;
	private Integer maxCount;
	private Integer campaignFeatures;
	private Integer entityId;
	private Integer param1;
	private Integer param2;
	private Integer param3;
	private Integer param4;
	private String strParam1;
	private String strParam2;
	private String strParam3;
	private Integer restSecs;
	private Integer restType;
	private String timerBegin;
	private Integer timerType;
	private String timerEnd;
	private Integer initSecs;
	private Integer entityType;

	// Constructors

	/** default constructor */
	public FieldSpawn() {
	}

	/** minimal constructor */
	public FieldSpawn(Integer id, Integer tagId, String preSpawanId, String nextSpawanId) {
		this.id = id;
		this.tagId = tagId;
		this.preSpawanId = preSpawanId;
		this.nextSpawanId = nextSpawanId;
	}

	/** full constructor */
	public FieldSpawn(Integer id, Integer mapid, Integer boundX, Integer boundY, Integer boundZ, Short initStatu,
			Integer tagId, String preSpawanId, String nextSpawanId, Integer wakeType, Integer wakeDelay,
			Integer toalCount, Integer maxCount, Integer campaignFeatures, Integer entityId, Integer param1,
			Integer param2, Integer param3, Integer param4, String strParam1, String strParam2, String strParam3,
			Integer restSecs, Integer restType, String timerBegin, Integer timerType, String timerEnd,
			Integer initSecs, Integer entityType) {
		this.id = id;
		this.mapid = mapid;
		this.boundX = boundX;
		this.boundY = boundY;
		this.boundZ = boundZ;
		this.initStatu = initStatu;
		this.tagId = tagId;
		this.preSpawanId = preSpawanId;
		this.nextSpawanId = nextSpawanId;
		this.wakeType = wakeType;
		this.wakeDelay = wakeDelay;
		this.toalCount = toalCount;
		this.maxCount = maxCount;
		this.campaignFeatures = campaignFeatures;
		this.entityId = entityId;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
		this.strParam1 = strParam1;
		this.strParam2 = strParam2;
		this.strParam3 = strParam3;
		this.restSecs = restSecs;
		this.restType = restType;
		this.timerBegin = timerBegin;
		this.timerType = timerType;
		this.timerEnd = timerEnd;
		this.initSecs = initSecs;
		this.entityType = entityType;
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

	@Column(name = "mapid")
	public Integer getMapid() {
		return this.mapid;
	}

	public void setMapid(Integer mapid) {
		this.mapid = mapid;
	}

	@Column(name = "bound_x")
	public Integer getBoundX() {
		return this.boundX;
	}

	public void setBoundX(Integer boundX) {
		this.boundX = boundX;
	}

	@Column(name = "bound_y")
	public Integer getBoundY() {
		return this.boundY;
	}

	public void setBoundY(Integer boundY) {
		this.boundY = boundY;
	}

	@Column(name = "bound_z")
	public Integer getBoundZ() {
		return this.boundZ;
	}

	public void setBoundZ(Integer boundZ) {
		this.boundZ = boundZ;
	}

	@Column(name = "initStatu")
	public Short getInitStatu() {
		return this.initStatu;
	}

	public void setInitStatu(Short initStatu) {
		this.initStatu = initStatu;
	}

	@Column(name = "tagId", nullable = false)
	public Integer getTagId() {
		return this.tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	@Column(name = "preSpawanId", nullable = false, length = 1255)
	public String getPreSpawanId() {
		return this.preSpawanId;
	}

	public void setPreSpawanId(String preSpawanId) {
		this.preSpawanId = preSpawanId;
	}

	@Column(name = "nextSpawanId", nullable = false, length = 1255)
	public String getNextSpawanId() {
		return this.nextSpawanId;
	}

	public void setNextSpawanId(String nextSpawanId) {
		this.nextSpawanId = nextSpawanId;
	}

	@Column(name = "wakeType")
	public Integer getWakeType() {
		return this.wakeType;
	}

	public void setWakeType(Integer wakeType) {
		this.wakeType = wakeType;
	}

	@Column(name = "wakeDelay")
	public Integer getWakeDelay() {
		return this.wakeDelay;
	}

	public void setWakeDelay(Integer wakeDelay) {
		this.wakeDelay = wakeDelay;
	}

	@Column(name = "toalCount")
	public Integer getToalCount() {
		return this.toalCount;
	}

	public void setToalCount(Integer toalCount) {
		this.toalCount = toalCount;
	}

	@Column(name = "maxCount")
	public Integer getMaxCount() {
		return this.maxCount;
	}

	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}

	@Column(name = "campaignFeatures")
	public Integer getCampaignFeatures() {
		return this.campaignFeatures;
	}

	public void setCampaignFeatures(Integer campaignFeatures) {
		this.campaignFeatures = campaignFeatures;
	}

	@Column(name = "entityId")
	public Integer getEntityId() {
		return this.entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	@Column(name = "param1")
	public Integer getParam1() {
		return this.param1;
	}

	public void setParam1(Integer param1) {
		this.param1 = param1;
	}

	@Column(name = "param2")
	public Integer getParam2() {
		return this.param2;
	}

	public void setParam2(Integer param2) {
		this.param2 = param2;
	}

	@Column(name = "param3")
	public Integer getParam3() {
		return this.param3;
	}

	public void setParam3(Integer param3) {
		this.param3 = param3;
	}

	@Column(name = "param4")
	public Integer getParam4() {
		return this.param4;
	}

	public void setParam4(Integer param4) {
		this.param4 = param4;
	}

	@Column(name = "strParam1")
	public String getStrParam1() {
		return this.strParam1;
	}

	public void setStrParam1(String strParam1) {
		this.strParam1 = strParam1;
	}

	@Column(name = "strParam2")
	public String getStrParam2() {
		return this.strParam2;
	}

	public void setStrParam2(String strParam2) {
		this.strParam2 = strParam2;
	}

	@Column(name = "strParam3")
	public String getStrParam3() {
		return this.strParam3;
	}

	public void setStrParam3(String strParam3) {
		this.strParam3 = strParam3;
	}

	@Column(name = "rest_secs")
	public Integer getRestSecs() {
		return this.restSecs;
	}

	public void setRestSecs(Integer restSecs) {
		this.restSecs = restSecs;
	}

	@Column(name = "rest_type")
	public Integer getRestType() {
		return this.restType;
	}

	public void setRestType(Integer restType) {
		this.restType = restType;
	}

	@Column(name = "timer_begin", length = 11)
	public String getTimerBegin() {
		return this.timerBegin;
	}

	public void setTimerBegin(String timerBegin) {
		this.timerBegin = timerBegin;
	}

	@Column(name = "timer_type")
	public Integer getTimerType() {
		return this.timerType;
	}

	public void setTimerType(Integer timerType) {
		this.timerType = timerType;
	}

	@Column(name = "timer_end", length = 11)
	public String getTimerEnd() {
		return this.timerEnd;
	}

	public void setTimerEnd(String timerEnd) {
		this.timerEnd = timerEnd;
	}

	@Column(name = "init_secs")
	public Integer getInitSecs() {
		return this.initSecs;
	}

	public void setInitSecs(Integer initSecs) {
		this.initSecs = initSecs;
	}

	@Column(name = "entity_type")
	public Integer getEntityType() {
		return this.entityType;
	}

	public void setEntityType(Integer entityType) {
		this.entityType = entityType;
	}

}