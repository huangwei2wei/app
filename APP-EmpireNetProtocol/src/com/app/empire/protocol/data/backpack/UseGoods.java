package com.app.empire.protocol.data.backpack;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class UseGoods extends AbstractData {
	private int id;// 物品流水id
	private int goodsNum; // 使用数量

	public UseGoods(int sessionId, int serial) {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_UseGoods, sessionId, serial);
	}
	public UseGoods() {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_UseGoods);
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
