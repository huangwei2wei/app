package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * EquipRefine entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "equip_refine", catalog = "game_config")
public class EquipRefine implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer quality;
	private Integer star;
	private Integer proAdd;
	private Integer exp;

	// Constructors

	/** default constructor */
	public EquipRefine() {
	}

	/** full constructor */
	public EquipRefine(Integer quality, Integer star, Integer proAdd, Integer exp) {
		this.quality = quality;
		this.star = star;
		this.proAdd = proAdd;
		this.exp = exp;
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

	@Column(name = "quality", nullable = false)
	public Integer getQuality() {
		return this.quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	@Column(name = "star", nullable = false)
	public Integer getStar() {
		return this.star;
	}

	public void setStar(Integer star) {
		this.star = star;
	}

	@Column(name = "pro_add", nullable = false)
	public Integer getProAdd() {
		return this.proAdd;
	}

	public void setProAdd(Integer proAdd) {
		this.proAdd = proAdd;
	}

	@Column(name = "exp", nullable = false)
	public Integer getExp() {
		return this.exp;
	}

	public void setExp(Integer exp) {
		this.exp = exp;
	}

}