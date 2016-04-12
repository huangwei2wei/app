package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * VipPrivilege entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "vip_privilege", catalog = "game_config")
public class VipPrivilege implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Integer condition;
	private Integer addEnergyTop;
	private String reward;
	private String describe;

	// Constructors

	/** default constructor */
	public VipPrivilege() {
	}

	/** full constructor */
	public VipPrivilege(Integer id, String name, Integer condition, Integer addEnergyTop, String reward, String describe) {
		this.id = id;
		this.name = name;
		this.condition = condition;
		this.addEnergyTop = addEnergyTop;
		this.reward = reward;
		this.describe = describe;
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

	@Column(name = "name", nullable = false, length = 36)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "condition", nullable = false)
	public Integer getCondition() {
		return this.condition;
	}

	public void setCondition(Integer condition) {
		this.condition = condition;
	}

	@Column(name = "add_energy_top", nullable = false)
	public Integer getAddEnergyTop() {
		return this.addEnergyTop;
	}

	public void setAddEnergyTop(Integer addEnergyTop) {
		this.addEnergyTop = addEnergyTop;
	}

	@Column(name = "reward", nullable = false, length = 512)
	public String getReward() {
		return this.reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	@Column(name = "describe", nullable = false, length = 65535)
	public String getDescribe() {
		return this.describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

}