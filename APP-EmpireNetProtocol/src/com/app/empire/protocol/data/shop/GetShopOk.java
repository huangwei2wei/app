package com.app.empire.protocol.data.shop;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 神秘商人
 * 
 * @author doter
 * 
 */

public class GetShopOk extends AbstractData {
	private int discount;// 折扣 为0 表示无折扣
	private long createTime;// 生成时间
	private int[] id;// 配置表id
	private int[] goodsId;// 物品id
	private int[] num;// 物品数量
	private int[] gold;// 消耗金币
	private int[] diamond;// 消耗钻石
	private int refreshNum;// 刷新次数

	public GetShopOk(int sessionId, int serial) {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_GetShopOk, sessionId, serial);
	}
	public GetShopOk() {
		super(Protocol.MAIN_SHOP, Protocol.SHOP_GetShopOk);
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int[] getId() {
		return id;
	}
	public void setId(int[] id) {
		this.id = id;
	}
	public int[] getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int[] goodsId) {
		this.goodsId = goodsId;
	}
	public int[] getNum() {
		return num;
	}
	public void setNum(int[] num) {
		this.num = num;
	}
	public int[] getGold() {
		return gold;
	}
	public void setGold(int[] gold) {
		this.gold = gold;
	}
	public int[] getDiamond() {
		return diamond;
	}
	public void setDiamond(int[] diamond) {
		this.diamond = diamond;
	}
	public int getRefreshNum() {
		return refreshNum;
	}
	public void setRefreshNum(int refreshNum) {
		this.refreshNum = refreshNum;
	}

}
