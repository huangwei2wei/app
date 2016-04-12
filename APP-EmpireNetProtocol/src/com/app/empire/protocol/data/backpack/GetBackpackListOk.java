package com.app.empire.protocol.data.backpack;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetBackpackListOk extends AbstractData {
	private int[] id;// 物品流水id
	private int[] goodsId; // 物品id
	private int[] goodsNum; // 物品数量
	private long[] time; // 更新时间

	public GetBackpackListOk(int sessionId, int serial) {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackListOk, sessionId, serial);
	}

	public GetBackpackListOk() {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_GetBackpackListOk);
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

	public int[] getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(int[] goodsNum) {
		this.goodsNum = goodsNum;
	}

	public long[] getTime() {
		return time;
	}

	public void setTime(long[] time) {
		this.time = time;
	}

}
