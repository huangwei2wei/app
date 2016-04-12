package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Monster entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "monster", catalog = "game_config")
public class Monster implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Integer monsterType;
	private Integer barrier;
	private String modelName;
	private String deathEffects;
	private String enterEffects;
	private String exitEffects;
	private Integer attackType;
	private Float enlarge;
	private Float selfScale;

	// Constructors

	/** default constructor */
	public Monster() {
	}

	/** full constructor */
	public Monster(String name, Integer monsterType, Integer barrier, String modelName, String deathEffects, String enterEffects, String exitEffects, Integer attackType, Float enlarge, Float selfScale) {
		this.name = name;
		this.monsterType = monsterType;
		this.barrier = barrier;
		this.modelName = modelName;
		this.deathEffects = deathEffects;
		this.enterEffects = enterEffects;
		this.exitEffects = exitEffects;
		this.attackType = attackType;
		this.enlarge = enlarge;
		this.selfScale = selfScale;
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

	@Column(name = "name", nullable = false, length = 125)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "monster_type", nullable = false)
	public Integer getMonsterType() {
		return this.monsterType;
	}

	public void setMonsterType(Integer monsterType) {
		this.monsterType = monsterType;
	}

	@Column(name = "barrier", nullable = false)
	public Integer getBarrier() {
		return this.barrier;
	}

	public void setBarrier(Integer barrier) {
		this.barrier = barrier;
	}

	@Column(name = "model_name", nullable = false, length = 65535)
	public String getModelName() {
		return this.modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	@Column(name = "death_effects", nullable = false, length = 65535)
	public String getDeathEffects() {
		return this.deathEffects;
	}

	public void setDeathEffects(String deathEffects) {
		this.deathEffects = deathEffects;
	}

	@Column(name = "enter_effects", nullable = false, length = 65535)
	public String getEnterEffects() {
		return this.enterEffects;
	}

	public void setEnterEffects(String enterEffects) {
		this.enterEffects = enterEffects;
	}

	@Column(name = "exit_effects", nullable = false, length = 65535)
	public String getExitEffects() {
		return this.exitEffects;
	}

	public void setExitEffects(String exitEffects) {
		this.exitEffects = exitEffects;
	}

	@Column(name = "attack_type", nullable = false)
	public Integer getAttackType() {
		return this.attackType;
	}

	public void setAttackType(Integer attackType) {
		this.attackType = attackType;
	}

	@Column(name = "enlarge", nullable = false, precision = 12, scale = 0)
	public Float getEnlarge() {
		return this.enlarge;
	}

	public void setEnlarge(Float enlarge) {
		this.enlarge = enlarge;
	}

	@Column(name = "self_scale", nullable = false, precision = 12, scale = 0)
	public Float getSelfScale() {
		return this.selfScale;
	}

	public void setSelfScale(Float selfScale) {
		this.selfScale = selfScale;
	}

}