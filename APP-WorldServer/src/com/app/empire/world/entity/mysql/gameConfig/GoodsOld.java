package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * GoodsOld entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "goods_old", catalog = "game_config")
public class GoodsOld implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer showId;
	private Integer goodsType;
	private Integer subType;
	private String name;
	private Integer quality;
	private Integer orderGoods;
	private Integer color;
	private Integer packShow;
	private Integer wrapNum;
	private Integer isUsed;
	private Integer isSell;
	private Integer delete;
	private String description;

	// Constructors

	/** default constructor */
	public GoodsOld() {
	}

	/** full constructor */
	public GoodsOld(Integer id, Integer showId, Integer goodsType, Integer subType, String name, Integer quality, Integer orderGoods, Integer color, Integer packShow, Integer wrapNum, Integer isUsed,
			Integer isSell, Integer delete, String description) {
		this.id = id;
		this.showId = showId;
		this.goodsType = goodsType;
		this.subType = subType;
		this.name = name;
		this.quality = quality;
		this.orderGoods = orderGoods;
		this.color = color;
		this.packShow = packShow;
		this.wrapNum = wrapNum;
		this.isUsed = isUsed;
		this.isSell = isSell;
		this.delete = delete;
		this.description = description;
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

	@Column(name = "show_id", nullable = false)
	public Integer getShowId() {
		return this.showId;
	}

	public void setShowId(Integer showId) {
		this.showId = showId;
	}

	@Column(name = "goods_type", nullable = false)
	public Integer getGoodsType() {
		return this.goodsType;
	}

	public void setGoodsType(Integer goodsType) {
		this.goodsType = goodsType;
	}

	@Column(name = "sub_type", nullable = false)
	public Integer getSubType() {
		return this.subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}

	@Column(name = "name", nullable = false, length = 128)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "quality", nullable = false)
	public Integer getQuality() {
		return this.quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	@Column(name = "order_goods", nullable = false)
	public Integer getOrderGoods() {
		return this.orderGoods;
	}

	public void setOrderGoods(Integer orderGoods) {
		this.orderGoods = orderGoods;
	}

	@Column(name = "color", nullable = false)
	public Integer getColor() {
		return this.color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	@Column(name = "pack_show", nullable = false)
	public Integer getPackShow() {
		return this.packShow;
	}

	public void setPackShow(Integer packShow) {
		this.packShow = packShow;
	}

	@Column(name = "wrap_num", nullable = false)
	public Integer getWrapNum() {
		return this.wrapNum;
	}

	public void setWrapNum(Integer wrapNum) {
		this.wrapNum = wrapNum;
	}

	@Column(name = "is_used", nullable = false)
	public Integer getIsUsed() {
		return this.isUsed;
	}

	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
	}

	@Column(name = "is_sell", nullable = false)
	public Integer getIsSell() {
		return this.isSell;
	}

	public void setIsSell(Integer isSell) {
		this.isSell = isSell;
	}

	@Column(name = "delete", nullable = false)
	public Integer getDelete() {
		return this.delete;
	}

	public void setDelete(Integer delete) {
		this.delete = delete;
	}

	@Column(name = "description", nullable = false, length = 1024)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}