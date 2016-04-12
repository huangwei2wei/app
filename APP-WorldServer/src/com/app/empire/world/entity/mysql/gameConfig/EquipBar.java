package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * EquipBar entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "equip_bar", catalog = "game_config")
public class EquipBar implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer heroType;
	private Integer stage;
	private String equip1;
	private String equip2;
	private String equip3;
	private String equip4;
	private String equip5;
	private String equip6;

	// Constructors

	/** default constructor */
	public EquipBar() {
	}

	/** full constructor */
	public EquipBar(Integer heroType, Integer stage, String equip1, String equip2, String equip3, String equip4, String equip5, String equip6) {
		this.heroType = heroType;
		this.stage = stage;
		this.equip1 = equip1;
		this.equip2 = equip2;
		this.equip3 = equip3;
		this.equip4 = equip4;
		this.equip5 = equip5;
		this.equip6 = equip6;
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

	@Column(name = "hero_type", nullable = false)
	public Integer getHeroType() {
		return this.heroType;
	}

	public void setHeroType(Integer heroType) {
		this.heroType = heroType;
	}

	@Column(name = "stage", nullable = false)
	public Integer getStage() {
		return this.stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	@Column(name = "equip1", nullable = false, length = 20)
	public String getEquip1() {
		return this.equip1;
	}

	public void setEquip1(String equip1) {
		this.equip1 = equip1;
	}

	@Column(name = "equip2", nullable = false, length = 20)
	public String getEquip2() {
		return this.equip2;
	}

	public void setEquip2(String equip2) {
		this.equip2 = equip2;
	}

	@Column(name = "equip3", nullable = false, length = 20)
	public String getEquip3() {
		return this.equip3;
	}

	public void setEquip3(String equip3) {
		this.equip3 = equip3;
	}

	@Column(name = "equip4", nullable = false, length = 20)
	public String getEquip4() {
		return this.equip4;
	}

	public void setEquip4(String equip4) {
		this.equip4 = equip4;
	}

	@Column(name = "equip5", nullable = false, length = 20)
	public String getEquip5() {
		return this.equip5;
	}

	public void setEquip5(String equip5) {
		this.equip5 = equip5;
	}

	@Column(name = "equip6", nullable = false, length = 20)
	public String getEquip6() {
		return this.equip6;
	}

	public void setEquip6(String equip6) {
		this.equip6 = equip6;
	}

}