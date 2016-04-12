package com.app.empire.protocol.data.equip;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 精炼装备
 */
public class RefineEquip extends AbstractData {
	int heroId; // 英雄流水id
	int rank; // 军阶
	int equipNo; // 精炼的装备栏编号
	int[] goodsId; // 玩家物品流水id
	int[] goodsNum; // 物品材料对应数量

	public RefineEquip(int sessionId, int serial) {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_RefineEquip, sessionId, serial);
	}

	public RefineEquip() {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_RefineEquip);
	}

	public int getHeroId() {
		return heroId;
	}

	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getEquipNo() {
		return equipNo;
	}

	public void setEquipNo(int equipNo) {
		this.equipNo = equipNo;
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

}
