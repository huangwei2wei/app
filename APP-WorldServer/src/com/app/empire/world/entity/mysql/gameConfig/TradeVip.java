package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TradeVip entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "trade_vip", catalog = "game_config")
public class TradeVip implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer vipLv;
	private Integer goldCount;
	private Integer goldDiamond;
	private Float goldCritMultiple;
	private Integer provisionsCount;
	private Integer provisionsDiamond;
	private Float provisionsCritMultiple;

	// Constructors

	/** default constructor */
	public TradeVip() {
	}

	/** minimal constructor */
	public TradeVip(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public TradeVip(Integer id, Integer vipLv, Integer goldCount, Integer goldDiamond, Float goldCritMultiple, Integer provisionsCount, Integer provisionsDiamond, Float provisionsCritMultiple) {
		this.id = id;
		this.vipLv = vipLv;
		this.goldCount = goldCount;
		this.goldDiamond = goldDiamond;
		this.goldCritMultiple = goldCritMultiple;
		this.provisionsCount = provisionsCount;
		this.provisionsDiamond = provisionsDiamond;
		this.provisionsCritMultiple = provisionsCritMultiple;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "vip_lv")
	public Integer getVipLv() {
		return this.vipLv;
	}

	public void setVipLv(Integer vipLv) {
		this.vipLv = vipLv;
	}

	@Column(name = "gold_count")
	public Integer getGoldCount() {
		return this.goldCount;
	}

	public void setGoldCount(Integer goldCount) {
		this.goldCount = goldCount;
	}

	@Column(name = "gold_diamond")
	public Integer getGoldDiamond() {
		return this.goldDiamond;
	}

	public void setGoldDiamond(Integer goldDiamond) {
		this.goldDiamond = goldDiamond;
	}

	@Column(name = "gold_crit_multiple", precision = 12, scale = 0)
	public Float getGoldCritMultiple() {
		return this.goldCritMultiple;
	}

	public void setGoldCritMultiple(Float goldCritMultiple) {
		this.goldCritMultiple = goldCritMultiple;
	}

	@Column(name = "provisions_count")
	public Integer getProvisionsCount() {
		return this.provisionsCount;
	}

	public void setProvisionsCount(Integer provisionsCount) {
		this.provisionsCount = provisionsCount;
	}

	@Column(name = "provisions_diamond")
	public Integer getProvisionsDiamond() {
		return this.provisionsDiamond;
	}

	public void setProvisionsDiamond(Integer provisionsDiamond) {
		this.provisionsDiamond = provisionsDiamond;
	}

	@Column(name = "provisions_crit_multiple", precision = 12, scale = 0)
	public Float getProvisionsCritMultiple() {
		return this.provisionsCritMultiple;
	}

	public void setProvisionsCritMultiple(Float provisionsCritMultiple) {
		this.provisionsCritMultiple = provisionsCritMultiple;
	}

}