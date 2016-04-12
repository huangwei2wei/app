package com.app.empire.protocol.data.backpack;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class UpdateGoodsData extends AbstractData {
	private int[] id;// 背包物品流水id
	private int[] goodsId;// 物品id
	private int[] goodsNum; // 背包物品当前数量,0表示删除
	private long[] upTime; // 更新时间

	public UpdateGoodsData(int sessionId, int serial) {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_UpdateGoodsData, sessionId, serial);
	}

	public UpdateGoodsData() {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_UpdateGoodsData);
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

	public long[] getUpTime() {
		return upTime;
	}

	public void setUpTime(long[] upTime) {
		this.upTime = upTime;
	}

}
