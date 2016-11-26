package com.app.empire.scene.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SkillBuffer entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "skill_buffer", catalog = "game_config")
public class SkillBuffer implements java.io.Serializable {

	// Fields

	private Integer templateId;
	private String bufferName;
	private Integer icon;
	private Integer isHelpful;
	private Integer type;
	private Integer fromType;
	private Integer level;
	private Integer targetType;
	private Integer exeWay;
	private Integer durableType;
	private Integer exeTime;
	private Integer exeCount;
	private Integer cooldown;
	private Integer overlayType;
	private Integer overlayWay;
	private Integer isTips;
	private Integer isSave;
	private Integer valueType;
	private Integer value;
	private Integer valuePercent;
	private Integer valueType1;
	private Integer value1;
	private Integer valuePercent1;
	private Integer status;
	private Integer delay;
	private Integer costCount;
	private Integer param1;
	private Integer param2;
	private Integer param3;
	private Integer param4;

	// Constructors

	/** default constructor */
	public SkillBuffer() {
	}

	/** minimal constructor */
	public SkillBuffer(Integer templateId) {
		this.templateId = templateId;
	}

	/** full constructor */
	public SkillBuffer(Integer templateId, String bufferName, Integer icon, Integer isHelpful, Integer type,
			Integer fromType, Integer level, Integer targetType, Integer exeWay, Integer durableType, Integer exeTime,
			Integer exeCount, Integer cooldown, Integer overlayType, Integer overlayWay, Integer isTips,
			Integer isSave, Integer valueType, Integer value, Integer valuePercent, Integer valueType1, Integer value1,
			Integer valuePercent1, Integer status, Integer delay, Integer costCount, Integer param1, Integer param2,
			Integer param3, Integer param4) {
		this.templateId = templateId;
		this.bufferName = bufferName;
		this.icon = icon;
		this.isHelpful = isHelpful;
		this.type = type;
		this.fromType = fromType;
		this.level = level;
		this.targetType = targetType;
		this.exeWay = exeWay;
		this.durableType = durableType;
		this.exeTime = exeTime;
		this.exeCount = exeCount;
		this.cooldown = cooldown;
		this.overlayType = overlayType;
		this.overlayWay = overlayWay;
		this.isTips = isTips;
		this.isSave = isSave;
		this.valueType = valueType;
		this.value = value;
		this.valuePercent = valuePercent;
		this.valueType1 = valueType1;
		this.value1 = value1;
		this.valuePercent1 = valuePercent1;
		this.status = status;
		this.delay = delay;
		this.costCount = costCount;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
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

	@Column(name = "bufferName")
	public String getBufferName() {
		return this.bufferName;
	}

	public void setBufferName(String bufferName) {
		this.bufferName = bufferName;
	}

	@Column(name = "icon")
	public Integer getIcon() {
		return this.icon;
	}

	public void setIcon(Integer icon) {
		this.icon = icon;
	}

	@Column(name = "isHelpful")
	public Integer getIsHelpful() {
		return this.isHelpful;
	}

	public void setIsHelpful(Integer isHelpful) {
		this.isHelpful = isHelpful;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "fromType")
	public Integer getFromType() {
		return this.fromType;
	}

	public void setFromType(Integer fromType) {
		this.fromType = fromType;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "targetType")
	public Integer getTargetType() {
		return this.targetType;
	}

	public void setTargetType(Integer targetType) {
		this.targetType = targetType;
	}

	@Column(name = "exeWay")
	public Integer getExeWay() {
		return this.exeWay;
	}

	public void setExeWay(Integer exeWay) {
		this.exeWay = exeWay;
	}

	@Column(name = "durableType")
	public Integer getDurableType() {
		return this.durableType;
	}

	public void setDurableType(Integer durableType) {
		this.durableType = durableType;
	}

	@Column(name = "exeTime")
	public Integer getExeTime() {
		return this.exeTime;
	}

	public void setExeTime(Integer exeTime) {
		this.exeTime = exeTime;
	}

	@Column(name = "exeCount")
	public Integer getExeCount() {
		return this.exeCount;
	}

	public void setExeCount(Integer exeCount) {
		this.exeCount = exeCount;
	}

	@Column(name = "cooldown")
	public Integer getCooldown() {
		return this.cooldown;
	}

	public void setCooldown(Integer cooldown) {
		this.cooldown = cooldown;
	}

	@Column(name = "overlayType")
	public Integer getOverlayType() {
		return this.overlayType;
	}

	public void setOverlayType(Integer overlayType) {
		this.overlayType = overlayType;
	}

	@Column(name = "overlayWay")
	public Integer getOverlayWay() {
		return this.overlayWay;
	}

	public void setOverlayWay(Integer overlayWay) {
		this.overlayWay = overlayWay;
	}

	@Column(name = "isTips")
	public Integer getIsTips() {
		return this.isTips;
	}

	public void setIsTips(Integer isTips) {
		this.isTips = isTips;
	}

	@Column(name = "isSave")
	public Integer getIsSave() {
		return this.isSave;
	}

	public void setIsSave(Integer isSave) {
		this.isSave = isSave;
	}

	@Column(name = "valueType")
	public Integer getValueType() {
		return this.valueType;
	}

	public void setValueType(Integer valueType) {
		this.valueType = valueType;
	}

	@Column(name = "value")
	public Integer getValue() {
		return this.value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Column(name = "valuePercent")
	public Integer getValuePercent() {
		return this.valuePercent;
	}

	public void setValuePercent(Integer valuePercent) {
		this.valuePercent = valuePercent;
	}

	@Column(name = "valueType1")
	public Integer getValueType1() {
		return this.valueType1;
	}

	public void setValueType1(Integer valueType1) {
		this.valueType1 = valueType1;
	}

	@Column(name = "value1")
	public Integer getValue1() {
		return this.value1;
	}

	public void setValue1(Integer value1) {
		this.value1 = value1;
	}

	@Column(name = "valuePercent1")
	public Integer getValuePercent1() {
		return this.valuePercent1;
	}

	public void setValuePercent1(Integer valuePercent1) {
		this.valuePercent1 = valuePercent1;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "delay")
	public Integer getDelay() {
		return this.delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	@Column(name = "costCount")
	public Integer getCostCount() {
		return this.costCount;
	}

	public void setCostCount(Integer costCount) {
		this.costCount = costCount;
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

}