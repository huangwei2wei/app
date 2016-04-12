package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Ai entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "ai", catalog = "game_config")
public class Ai implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer type;
	private Float guardArea;
	private Float patrolArea;
	private Float stayTime;
	private Float attMoveSpeed;
	private Float heroTimeA;
	private Float heroTimeB;
	private Float nonHeroTimeA;
	private Float nonHeroTimeB;

	// Constructors

	/** default constructor */
	public Ai() {
	}

	/** full constructor */
	public Ai(Integer type, Float guardArea, Float patrolArea, Float stayTime, Float attMoveSpeed, Float heroTimeA, Float heroTimeB, Float nonHeroTimeA, Float nonHeroTimeB) {
		this.type = type;
		this.guardArea = guardArea;
		this.patrolArea = patrolArea;
		this.stayTime = stayTime;
		this.attMoveSpeed = attMoveSpeed;
		this.heroTimeA = heroTimeA;
		this.heroTimeB = heroTimeB;
		this.nonHeroTimeA = nonHeroTimeA;
		this.nonHeroTimeB = nonHeroTimeB;
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

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "guard_area", precision = 12, scale = 0)
	public Float getGuardArea() {
		return this.guardArea;
	}

	public void setGuardArea(Float guardArea) {
		this.guardArea = guardArea;
	}

	@Column(name = "patrol_area", precision = 12, scale = 0)
	public Float getPatrolArea() {
		return this.patrolArea;
	}

	public void setPatrolArea(Float patrolArea) {
		this.patrolArea = patrolArea;
	}

	@Column(name = "stay_time", precision = 12, scale = 0)
	public Float getStayTime() {
		return this.stayTime;
	}

	public void setStayTime(Float stayTime) {
		this.stayTime = stayTime;
	}

	@Column(name = "att_move_speed", precision = 12, scale = 0)
	public Float getAttMoveSpeed() {
		return this.attMoveSpeed;
	}

	public void setAttMoveSpeed(Float attMoveSpeed) {
		this.attMoveSpeed = attMoveSpeed;
	}

	@Column(name = "hero_time_a", precision = 12, scale = 0)
	public Float getHeroTimeA() {
		return this.heroTimeA;
	}

	public void setHeroTimeA(Float heroTimeA) {
		this.heroTimeA = heroTimeA;
	}

	@Column(name = "hero_time_b", precision = 12, scale = 0)
	public Float getHeroTimeB() {
		return this.heroTimeB;
	}

	public void setHeroTimeB(Float heroTimeB) {
		this.heroTimeB = heroTimeB;
	}

	@Column(name = "non_hero_time_a", precision = 12, scale = 0)
	public Float getNonHeroTimeA() {
		return this.nonHeroTimeA;
	}

	public void setNonHeroTimeA(Float nonHeroTimeA) {
		this.nonHeroTimeA = nonHeroTimeA;
	}

	@Column(name = "non_hero_time_b", precision = 12, scale = 0)
	public Float getNonHeroTimeB() {
		return this.nonHeroTimeB;
	}

	public void setNonHeroTimeB(Float nonHeroTimeB) {
		this.nonHeroTimeB = nonHeroTimeB;
	}

}