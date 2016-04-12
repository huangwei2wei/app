package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Base3Mall entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "base3_mall", catalog = "game_config")
public class Base3Mall implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer mallType;
	private Integer sort;
	private Integer goodsExtId;
	private Boolean type;
	private Integer money;
	private Integer moneyType;
	private Integer sum;
	private Integer stintSum;
	private Integer colour;
	private Integer lv;
	private String randP;
	private Float rebate;
	private Integer status;

	// Constructors

	/** default constructor */
	public Base3Mall() {
	}

	/** full constructor */
	public Base3Mall(Integer mallType, Integer sort, Integer goodsExtId, Boolean type, Integer money, Integer moneyType, Integer sum, Integer stintSum, Integer colour, Integer lv, String randP,
			Float rebate, Integer status) {
		this.mallType = mallType;
		this.sort = sort;
		this.goodsExtId = goodsExtId;
		this.type = type;
		this.money = money;
		this.moneyType = moneyType;
		this.sum = sum;
		this.stintSum = stintSum;
		this.colour = colour;
		this.lv = lv;
		this.randP = randP;
		this.rebate = rebate;
		this.status = status;
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

	@Column(name = "mall_type", nullable = false)
	public Integer getMallType() {
		return this.mallType;
	}

	public void setMallType(Integer mallType) {
		this.mallType = mallType;
	}

	@Column(name = "sort", nullable = false)
	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name = "goods_ext_id", nullable = false)
	public Integer getGoodsExtId() {
		return this.goodsExtId;
	}

	public void setGoodsExtId(Integer goodsExtId) {
		this.goodsExtId = goodsExtId;
	}

	@Column(name = "type", nullable = false)
	public Boolean getType() {
		return this.type;
	}

	public void setType(Boolean type) {
		this.type = type;
	}

	@Column(name = "money", nullable = false)
	public Integer getMoney() {
		return this.money;
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

	@Column(name = "money_type", nullable = false)
	public Integer getMoneyType() {
		return this.moneyType;
	}

	public void setMoneyType(Integer moneyType) {
		this.moneyType = moneyType;
	}

	@Column(name = "sum", nullable = false)
	public Integer getSum() {
		return this.sum;
	}

	public void setSum(Integer sum) {
		this.sum = sum;
	}

	@Column(name = "stint_sum", nullable = false)
	public Integer getStintSum() {
		return this.stintSum;
	}

	public void setStintSum(Integer stintSum) {
		this.stintSum = stintSum;
	}

	@Column(name = "colour", nullable = false)
	public Integer getColour() {
		return this.colour;
	}

	public void setColour(Integer colour) {
		this.colour = colour;
	}

	@Column(name = "lv", nullable = false)
	public Integer getLv() {
		return this.lv;
	}

	public void setLv(Integer lv) {
		this.lv = lv;
	}

	@Column(name = "rand_p", nullable = false, length = 32)
	public String getRandP() {
		return this.randP;
	}

	public void setRandP(String randP) {
		this.randP = randP;
	}

	@Column(name = "rebate", nullable = false, precision = 12, scale = 0)
	public Float getRebate() {
		return this.rebate;
	}

	public void setRebate(Float rebate) {
		this.rebate = rebate;
	}

	@Column(name = "status", nullable = false)
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}