package com.app.empire.world.entity.mongo;

/**
 * 商店中的物品
 * 
 * @author doter
 * 
 */
public class Product {
	private int tableId;// 配置表id
	private int goodsId;// 物品id
	private int num;// 现有物品数量
	private int gold;// 需要金币
	private int diamond;// 需要砖石

	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getDiamond() {
		return diamond;
	}
	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

}
