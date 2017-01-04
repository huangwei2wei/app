package com.app.db.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SnareInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "snare_info", catalog = "game_config")
public class SnareInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer skinId;
	private Integer type;
	private Integer lifetime;
	private Integer validCount;
	private Integer exeWay;
	private Integer coolDown;
	private Integer checkX;
	private Integer checkZ;
	private Integer hp;
	private Integer target;
	private Integer bornType;
	private Integer bornAngle;
	private Integer bornlength;
	private Integer moveType;
	private Integer moveSpeed;
	private Integer distance;
	private Integer lockingType;
	private Integer stateId;
	private String addBuffers;
	private Integer soulPercent;
	private Integer soulValue;
	private Integer bloodPercent;
	private Integer bloodValue;

	// Constructors

	/** default constructor */
	public SnareInfo() {
	}

	/** minimal constructor */
	public SnareInfo(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public SnareInfo(Integer id, Integer skinId, Integer type, Integer lifetime, Integer validCount, Integer exeWay, Integer coolDown, Integer checkX, Integer checkZ, Integer hp,
			Integer target, Integer bornType, Integer bornAngle, Integer bornlength, Integer moveType, Integer moveSpeed, Integer distance, Integer lockingType, Integer stateId,
			String addBuffers, Integer soulPercent, Integer soulValue, Integer bloodPercent, Integer bloodValue) {
		this.id = id;
		this.skinId = skinId;
		this.type = type;
		this.lifetime = lifetime;
		this.validCount = validCount;
		this.exeWay = exeWay;
		this.coolDown = coolDown;
		this.checkX = checkX;
		this.checkZ = checkZ;
		this.hp = hp;
		this.target = target;
		this.bornType = bornType;
		this.bornAngle = bornAngle;
		this.bornlength = bornlength;
		this.moveType = moveType;
		this.moveSpeed = moveSpeed;
		this.distance = distance;
		this.lockingType = lockingType;
		this.stateId = stateId;
		this.addBuffers = addBuffers;
		this.soulPercent = soulPercent;
		this.soulValue = soulValue;
		this.bloodPercent = bloodPercent;
		this.bloodValue = bloodValue;
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

	@Column(name = "skinId")
	public Integer getSkinId() {
		return this.skinId;
	}

	public void setSkinId(Integer skinId) {
		this.skinId = skinId;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "lifetime")
	public Integer getLifetime() {
		return this.lifetime;
	}

	public void setLifetime(Integer lifetime) {
		this.lifetime = lifetime;
	}

	@Column(name = "validCount")
	public Integer getValidCount() {
		return this.validCount;
	}

	public void setValidCount(Integer validCount) {
		this.validCount = validCount;
	}

	@Column(name = "exeWay")
	public Integer getExeWay() {
		return this.exeWay;
	}

	public void setExeWay(Integer exeWay) {
		this.exeWay = exeWay;
	}

	@Column(name = "coolDown")
	public Integer getCoolDown() {
		return this.coolDown;
	}

	public void setCoolDown(Integer coolDown) {
		this.coolDown = coolDown;
	}

	@Column(name = "checkX")
	public Integer getCheckX() {
		return this.checkX;
	}

	public void setCheckX(Integer checkX) {
		this.checkX = checkX;
	}

	@Column(name = "checkZ")
	public Integer getCheckZ() {
		return this.checkZ;
	}

	public void setCheckZ(Integer checkZ) {
		this.checkZ = checkZ;
	}

	@Column(name = "hp")
	public Integer getHp() {
		return this.hp;
	}

	public void setHp(Integer hp) {
		this.hp = hp;
	}

	@Column(name = "target")
	public Integer getTarget() {
		return this.target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}

	@Column(name = "bornType")
	public Integer getBornType() {
		return this.bornType;
	}

	public void setBornType(Integer bornType) {
		this.bornType = bornType;
	}

	@Column(name = "bornAngle")
	public Integer getBornAngle() {
		return this.bornAngle;
	}

	public void setBornAngle(Integer bornAngle) {
		this.bornAngle = bornAngle;
	}

	@Column(name = "bornlength")
	public Integer getBornlength() {
		return this.bornlength;
	}

	public void setBornlength(Integer bornlength) {
		this.bornlength = bornlength;
	}

	@Column(name = "moveType")
	public Integer getMoveType() {
		return this.moveType;
	}

	public void setMoveType(Integer moveType) {
		this.moveType = moveType;
	}

	@Column(name = "moveSpeed")
	public Integer getMoveSpeed() {
		return this.moveSpeed;
	}

	public void setMoveSpeed(Integer moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	@Column(name = "distance")
	public Integer getDistance() {
		return this.distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	@Column(name = "lockingType")
	public Integer getLockingType() {
		return this.lockingType;
	}

	public void setLockingType(Integer lockingType) {
		this.lockingType = lockingType;
	}

	@Column(name = "stateId")
	public Integer getStateId() {
		return this.stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	@Column(name = "addBuffers")
	public String getAddBuffers() {
		return this.addBuffers;
	}

	public void setAddBuffers(String addBuffers) {
		this.addBuffers = addBuffers;
	}

	@Column(name = "soulPercent")
	public Integer getSoulPercent() {
		return this.soulPercent;
	}

	public void setSoulPercent(Integer soulPercent) {
		this.soulPercent = soulPercent;
	}

	@Column(name = "soulValue")
	public Integer getSoulValue() {
		return this.soulValue;
	}

	public void setSoulValue(Integer soulValue) {
		this.soulValue = soulValue;
	}

	@Column(name = "bloodPercent")
	public Integer getBloodPercent() {
		return this.bloodPercent;
	}

	public void setBloodPercent(Integer bloodPercent) {
		this.bloodPercent = bloodPercent;
	}

	@Column(name = "bloodValue")
	public Integer getBloodValue() {
		return this.bloodValue;
	}

	public void setBloodValue(Integer bloodValue) {
		this.bloodValue = bloodValue;
	}

}