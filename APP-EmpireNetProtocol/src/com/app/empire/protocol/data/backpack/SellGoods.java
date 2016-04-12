package com.app.empire.protocol.data.backpack;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class SellGoods extends AbstractData {
	private int id;// 背包物品流水id
	private int goodsNum; // 背包物品数量

	public SellGoods(int sessionId, int serial) {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_SellGoods, sessionId, serial);
	}

	public SellGoods() {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_SellGoods);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}

}
