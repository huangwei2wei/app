package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Skill entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "skill", catalog = "game_config")
public class Skill implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private String iconId;
	private Integer type;
	private Integer maxLv;
	private Integer damageType;
	private String actionName;
	private Float inDisplacement;
	private Float inDisplacementTime;
	private Float paDisplacement;
	private Float paDisplacementTime;
	private String atteffectName;
	private Integer atteffectType;
	private String hiteffectName;
	private Float attObjectSpeed;
	private Float isshakeTime;
	private Float isshakeRange;
	private String description;

	// Constructors

	/** default constructor */
	public Skill() {
	}

	/** minimal constructor */
	public Skill(Integer maxLv, String atteffectName, Integer atteffectType, Float attObjectSpeed, Float isshakeRange) {
		this.maxLv = maxLv;
		this.atteffectName = atteffectName;
		this.atteffectType = atteffectType;
		this.attObjectSpeed = attObjectSpeed;
		this.isshakeRange = isshakeRange;
	}

	/** full constructor */
	public Skill(String name, String iconId, Integer type, Integer maxLv, Integer damageType, String actionName, Float inDisplacement, Float inDisplacementTime, Float paDisplacement,
			Float paDisplacementTime, String atteffectName, Integer atteffectType, String hiteffectName, Float attObjectSpeed, Float isshakeTime, Float isshakeRange, String description) {
		this.name = name;
		this.iconId = iconId;
		this.type = type;
		this.maxLv = maxLv;
		this.damageType = damageType;
		this.actionName = actionName;
		this.inDisplacement = inDisplacement;
		this.inDisplacementTime = inDisplacementTime;
		this.paDisplacement = paDisplacement;
		this.paDisplacementTime = paDisplacementTime;
		this.atteffectName = atteffectName;
		this.atteffectType = atteffectType;
		this.hiteffectName = hiteffectName;
		this.attObjectSpeed = attObjectSpeed;
		this.isshakeTime = isshakeTime;
		this.isshakeRange = isshakeRange;
		this.description = description;
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

	@Column(name = "name", length = 7)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "icon_id", length = 65535)
	public String getIconId() {
		return this.iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "max_lv", nullable = false)
	public Integer getMaxLv() {
		return this.maxLv;
	}

	public void setMaxLv(Integer maxLv) {
		this.maxLv = maxLv;
	}

	@Column(name = "damage_type")
	public Integer getDamageType() {
		return this.damageType;
	}

	public void setDamageType(Integer damageType) {
		this.damageType = damageType;
	}

	@Column(name = "action_name", length = 64)
	public String getActionName() {
		return this.actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	@Column(name = "in_displacement", precision = 12, scale = 0)
	public Float getInDisplacement() {
		return this.inDisplacement;
	}

	public void setInDisplacement(Float inDisplacement) {
		this.inDisplacement = inDisplacement;
	}

	@Column(name = "in_displacement_time", precision = 12, scale = 0)
	public Float getInDisplacementTime() {
		return this.inDisplacementTime;
	}

	public void setInDisplacementTime(Float inDisplacementTime) {
		this.inDisplacementTime = inDisplacementTime;
	}

	@Column(name = "pa_displacement", precision = 12, scale = 0)
	public Float getPaDisplacement() {
		return this.paDisplacement;
	}

	public void setPaDisplacement(Float paDisplacement) {
		this.paDisplacement = paDisplacement;
	}

	@Column(name = "pa_displacement_time", precision = 12, scale = 0)
	public Float getPaDisplacementTime() {
		return this.paDisplacementTime;
	}

	public void setPaDisplacementTime(Float paDisplacementTime) {
		this.paDisplacementTime = paDisplacementTime;
	}

	@Column(name = "atteffect_name", nullable = false, length = 65535)
	public String getAtteffectName() {
		return this.atteffectName;
	}

	public void setAtteffectName(String atteffectName) {
		this.atteffectName = atteffectName;
	}

	@Column(name = "atteffect_type", nullable = false)
	public Integer getAtteffectType() {
		return this.atteffectType;
	}

	public void setAtteffectType(Integer atteffectType) {
		this.atteffectType = atteffectType;
	}

	@Column(name = "hiteffect_name", length = 65535)
	public String getHiteffectName() {
		return this.hiteffectName;
	}

	public void setHiteffectName(String hiteffectName) {
		this.hiteffectName = hiteffectName;
	}

	@Column(name = "att_object_speed", nullable = false, precision = 12, scale = 0)
	public Float getAttObjectSpeed() {
		return this.attObjectSpeed;
	}

	public void setAttObjectSpeed(Float attObjectSpeed) {
		this.attObjectSpeed = attObjectSpeed;
	}

	@Column(name = "isshake_time", precision = 12, scale = 0)
	public Float getIsshakeTime() {
		return this.isshakeTime;
	}

	public void setIsshakeTime(Float isshakeTime) {
		this.isshakeTime = isshakeTime;
	}

	@Column(name = "isshake_range", nullable = false, precision = 12, scale = 0)
	public Float getIsshakeRange() {
		return this.isshakeRange;
	}

	public void setIsshakeRange(Float isshakeRange) {
		this.isshakeRange = isshakeRange;
	}

	@Column(name = "description", length = 65535)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}