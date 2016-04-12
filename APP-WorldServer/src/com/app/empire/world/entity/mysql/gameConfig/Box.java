package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Box entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "box", catalog = "game_config")
public class Box implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer type;
	private Integer boxId;
	private Integer goodsId;
	private Float random;

	// Constructors

	/** default constructor */
	public Box() {
	}

	/** minimal constructor */
	public Box(Integer type) {
		this.type = type;
	}

	/** full constructor */
	public Box(Integer type, Integer boxId, Integer goodsId, Float random) {
		this.type = type;
		this.boxId = boxId;
		this.goodsId = goodsId;
		this.random = random;
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

	@Column(name = "type", nullable = false)
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "box_id")
	public Integer getBoxId() {
		return this.boxId;
	}

	public void setBoxId(Integer boxId) {
		this.boxId = boxId;
	}

	@Column(name = "goods_id")
	public Integer getGoodsId() {
		return this.goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}

	@Column(name = "random", precision = 12, scale = 0)
	public Float getRandom() {
		return this.random;
	}

	public void setRandom(Float random) {
		this.random = random;
	}

}