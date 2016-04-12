package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Store entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "store", catalog = "game_config")
public class Store implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer goodsId;
	private Integer grade;
	private Integer sum;
	private Integer costGold;
	private Integer costDiamond;
	private Integer random;
	private Integer score;
	private Integer teamLv;

	// Constructors

	/** default constructor */
	public Store() {
	}

	/** full constructor */
	public Store(Integer goodsId, Integer grade, Integer sum, Integer costGold, Integer costDiamond, Integer random, Integer score, Integer teamLv) {
		this.goodsId = goodsId;
		this.grade = grade;
		this.sum = sum;
		this.costGold = costGold;
		this.costDiamond = costDiamond;
		this.random = random;
		this.score = score;
		this.teamLv = teamLv;
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

	@Column(name = "goods_id")
	public Integer getGoodsId() {
		return this.goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}

	@Column(name = "grade")
	public Integer getGrade() {
		return this.grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	@Column(name = "sum")
	public Integer getSum() {
		return this.sum;
	}

	public void setSum(Integer sum) {
		this.sum = sum;
	}

	@Column(name = "cost_gold")
	public Integer getCostGold() {
		return this.costGold;
	}

	public void setCostGold(Integer costGold) {
		this.costGold = costGold;
	}

	@Column(name = "cost_diamond")
	public Integer getCostDiamond() {
		return this.costDiamond;
	}

	public void setCostDiamond(Integer costDiamond) {
		this.costDiamond = costDiamond;
	}

	@Column(name = "random")
	public Integer getRandom() {
		return this.random;
	}

	public void setRandom(Integer random) {
		this.random = random;
	}

	@Column(name = "score")
	public Integer getScore() {
		return this.score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	@Column(name = "team_lv")
	public Integer getTeamLv() {
		return this.teamLv;
	}

	public void setTeamLv(Integer teamLv) {
		this.teamLv = teamLv;
	}

}