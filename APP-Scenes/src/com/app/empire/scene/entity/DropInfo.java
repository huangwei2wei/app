package com.app.empire.scene.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DropInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "drop_info", catalog = "game_config")
public class DropInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private Short type;
	private Integer repeat;
	private Short limitType;
	private String startTime;
	private String endTime;
	private Short visibleType;

	// Constructors

	/** default constructor */
	public DropInfo() {
	}

	/** full constructor */
	public DropInfo(Integer id, Short type, Integer repeat, Short limitType, String startTime, String endTime, Short visibleType) {
		this.id = id;
		this.type = type;
		this.repeat = repeat;
		this.limitType = limitType;
		this.startTime = startTime;
		this.endTime = endTime;
		this.visibleType = visibleType;
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

	@Column(name = "type", nullable = false)
	public Short getType() {
		return this.type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	@Column(name = "repeat", nullable = false)
	public Integer getRepeat() {
		return this.repeat;
	}

	public void setRepeat(Integer repeat) {
		this.repeat = repeat;
	}

	@Column(name = "limitType", nullable = false)
	public Short getLimitType() {
		return this.limitType;
	}

	public void setLimitType(Short limitType) {
		this.limitType = limitType;
	}

	@Column(name = "startTime", nullable = false)
	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "endTime", nullable = false)
	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name = "visibleType", nullable = false)
	public Short getVisibleType() {
		return this.visibleType;
	}

	public void setVisibleType(Short visibleType) {
		this.visibleType = visibleType;
	}

}