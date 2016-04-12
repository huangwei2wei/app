package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TradeNpc entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "trade_npc", catalog = "game_config")
public class TradeNpc implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer npcLv;
	private Integer teamLv;
	private Integer type;
	private Integer output;
	private Integer maxCapacity;
	private Integer baseValues;
	private Float critRate;
	private Integer consume;
	private Integer time;
	private String info;

	// Constructors

	/** default constructor */
	public TradeNpc() {
	}

	/** minimal constructor */
	public TradeNpc(Integer consume, Integer time, String info) {
		this.consume = consume;
		this.time = time;
		this.info = info;
	}

	/** full constructor */
	public TradeNpc(Integer npcLv, Integer teamLv, Integer type, Integer output, Integer maxCapacity, Integer baseValues, Float critRate, Integer consume, Integer time, String info) {
		this.npcLv = npcLv;
		this.teamLv = teamLv;
		this.type = type;
		this.output = output;
		this.maxCapacity = maxCapacity;
		this.baseValues = baseValues;
		this.critRate = critRate;
		this.consume = consume;
		this.time = time;
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

	@Column(name = "npc_lv")
	public Integer getNpcLv() {
		return this.npcLv;
	}

	public void setNpcLv(Integer npcLv) {
		this.npcLv = npcLv;
	}

	@Column(name = "team_lv")
	public Integer getTeamLv() {
		return this.teamLv;
	}

	public void setTeamLv(Integer teamLv) {
		this.teamLv = teamLv;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "output")
	public Integer getOutput() {
		return this.output;
	}

	public void setOutput(Integer output) {
		this.output = output;
	}

	@Column(name = "max_capacity")
	public Integer getMaxCapacity() {
		return this.maxCapacity;
	}

	public void setMaxCapacity(Integer maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	@Column(name = "base_values")
	public Integer getBaseValues() {
		return this.baseValues;
	}

	public void setBaseValues(Integer baseValues) {
		this.baseValues = baseValues;
	}

	@Column(name = "crit_rate", precision = 12, scale = 0)
	public Float getCritRate() {
		return this.critRate;
	}

	public void setCritRate(Float critRate) {
		this.critRate = critRate;
	}

	@Column(name = "consume", nullable = false)
	public Integer getConsume() {
		return this.consume;
	}

	public void setConsume(Integer consume) {
		this.consume = consume;
	}

	@Column(name = "time", nullable = false)
	public Integer getTime() {
		return this.time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	@Column(name = "info", nullable = false, length = 65535)
	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}