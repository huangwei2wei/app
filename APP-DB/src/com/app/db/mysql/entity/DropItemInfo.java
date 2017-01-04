package com.app.db.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DropItemInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "drop_item_info", catalog = "game_config")
public class DropItemInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer poolId;
	private Integer itemId;
	private Integer count;
	private Integer weight;
	private Integer sendNotice;

	// Constructors

	/** default constructor */
	public DropItemInfo() {
	}

	/** full constructor */
	public DropItemInfo(Integer id, Integer poolId, Integer itemId, Integer count, Integer weight, Integer sendNotice) {
		this.id = id;
		this.poolId = poolId;
		this.itemId = itemId;
		this.count = count;
		this.weight = weight;
		this.sendNotice = sendNotice;
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

	@Column(name = "poolId", nullable = false)
	public Integer getPoolId() {
		return this.poolId;
	}

	public void setPoolId(Integer poolId) {
		this.poolId = poolId;
	}

	@Column(name = "itemId", nullable = false)
	public Integer getItemId() {
		return this.itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	@Column(name = "count", nullable = false)
	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Column(name = "weight", nullable = false)
	public Integer getWeight() {
		return this.weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	@Column(name = "sendNotice", nullable = false)
	public Integer getSendNotice() {
		return this.sendNotice;
	}

	public void setSendNotice(Integer sendNotice) {
		this.sendNotice = sendNotice;
	}

}