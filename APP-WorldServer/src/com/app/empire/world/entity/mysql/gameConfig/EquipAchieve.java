package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * EquipAchieve entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "equip_achieve", catalog = "game_config")
public class EquipAchieve implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Integer rank;
	private Integer achieveType;
	private Integer num;
	private Integer proAdd;
	private String info;

	// Constructors

	/** default constructor */
	public EquipAchieve() {
	}

	/** full constructor */
	public EquipAchieve(String name, Integer rank, Integer achieveType, Integer num, Integer proAdd, String info) {
		this.name = name;
		this.rank = rank;
		this.achieveType = achieveType;
		this.num = num;
		this.proAdd = proAdd;
		this.info = info;
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

	@Column(name = "name", nullable = false, length = 65535)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "rank", nullable = false)
	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Column(name = "achieve_type", nullable = false)
	public Integer getAchieveType() {
		return this.achieveType;
	}

	public void setAchieveType(Integer achieveType) {
		this.achieveType = achieveType;
	}

	@Column(name = "num", nullable = false)
	public Integer getNum() {
		return this.num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	@Column(name = "pro_add", nullable = false)
	public Integer getProAdd() {
		return this.proAdd;
	}

	public void setProAdd(Integer proAdd) {
		this.proAdd = proAdd;
	}

	@Column(name = "info", nullable = false, length = 65535)
	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}