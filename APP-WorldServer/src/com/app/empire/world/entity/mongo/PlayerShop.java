package com.app.empire.world.entity.mongo;

import java.util.Date;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.app.db.mongo.entity.IEntity;

/**
 * 玩家商店
 * 
 * @author doter
 * 
 */
@Document(collection = "player_shop")
public class PlayerShop extends IEntity {
	private int playerId;// 玩家角色id
	private int shopType;// 类型 1、神秘商店
	private int discount;// 折扣
	private Date createTime;// 生成时间
	private Map<Integer, Product> products;// 配置表id->物品
	private Map<String, Integer> refreshNum;// 刷新次数 日期->次数

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getShopType() {
		return shopType;
	}
	public void setShopType(int shopType) {
		this.shopType = shopType;
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Map<Integer, Product> getProducts() {
		return products;
	}
	public void setProducts(Map<Integer, Product> products) {
		this.products = products;
	}
	public Map<String, Integer> getRefreshNum() {
		return refreshNum;
	}
	public void setRefreshNum(Map<String, Integer> refreshNum) {
		this.refreshNum = refreshNum;
	}

}
