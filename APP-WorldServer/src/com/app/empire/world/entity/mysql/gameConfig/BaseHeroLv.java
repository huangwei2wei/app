package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BaseHeroLv entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "base_hero_lv", catalog = "game_config")
public class BaseHeroLv implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer lv;
	private Integer experience;

	// Constructors

	/** default constructor */
	public BaseHeroLv() {
	}

	/** full constructor */
	public BaseHeroLv(Integer lv, Integer experience) {
		this.lv = lv;
		this.experience = experience;
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

	@Column(name = "lv", nullable = false)
	public Integer getLv() {
		return this.lv;
	}

	public void setLv(Integer lv) {
		this.lv = lv;
	}

	@Column(name = "experience", nullable = false)
	public Integer getExperience() {
		return this.experience;
	}

	public void setExperience(Integer experience) {
		this.experience = experience;
	}

}