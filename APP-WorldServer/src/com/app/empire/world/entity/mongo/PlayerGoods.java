package com.app.empire.world.entity.mongo;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.app.db.mongo.entity.IEntity;

/**
 * 物品表
 * 
 * @author doter
 * 
 */

@Document(collection = "player_goods")
public class PlayerGoods extends IEntity {
	private int playerId;// 用户id
	private int goodsId; // 物品基表id
	private String goodsName;// 物品名称
	// private int goodsExtId; //物品扩展id
	private int goodsNum; // 物品数量
	private int goodsQuality; // 物品品质
	private int goodsStar; // 物品星级
	private int goodsType; // 物品类型
	private int subType; // 物品子类型
	private String property;// 属性
	private Date dateTime; // 添加、更新时间

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public int getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}
	public int getGoodsQuality() {
		return goodsQuality;
	}
	public void setGoodsQuality(int goodsQuality) {
		this.goodsQuality = goodsQuality;
	}
	public int getGoodsType() {
		return goodsType;
	}
	public void setGoodsType(int goodsType) {
		this.goodsType = goodsType;
	}

	public int getSubType() {
		return subType;
	}
	public void setSubType(int subType) {
		this.subType = subType;
	}
	public int getGoodsStar() {
		return goodsStar;
	}
	public void setGoodsStar(int goodsStar) {
		this.goodsStar = goodsStar;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}

}
