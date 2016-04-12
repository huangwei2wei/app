package com.app.empire.world.entity.mongo;

/**
 * 装备栏中装备信息
 */

public class HeroEquipGoods {
	private int goodsId; // 物品基表id
	private int goodsQuality; // 物品品质：0.白 1.绿 2.蓝 3.紫 4.橙
	private int goodsStar; // 物品精炼星级
	private int goodsExp; // 装备精炼经验值
	private String property;// 属性
	private int proAdd; // 精炼属性加成

	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public int getGoodsQuality() {
		return goodsQuality;
	}
	public void setGoodsQuality(int goodsQuality) {
		this.goodsQuality = goodsQuality;
	}
	public int getGoodsStar() {
		return goodsStar;
	}
	public void setGoodsStar(int goodsStar) {
		this.goodsStar = goodsStar;
	}
	public int getGoodsExp() {
		return goodsExp;
	}
	public void setGoodsExp(int goodsExp) {
		this.goodsExp = goodsExp;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public int getProAdd() {
		return proAdd;
	}
	public void setProAdd(int proAdd) {
		this.proAdd = proAdd;
	}

}
