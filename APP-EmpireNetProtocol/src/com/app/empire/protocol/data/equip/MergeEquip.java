package com.app.empire.protocol.data.equip;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 合成装备并穿戴
 */
public class MergeEquip extends AbstractData {
	int heroId; // 英雄流水id
	int rank; // 军阶
	int equipNo; // 合成的装备栏编号
	int goodsId; // 要合成的装备id

	public MergeEquip(int sessionId, int serial) {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_MergeEquip, sessionId, serial);
	}

	public MergeEquip() {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_MergeEquip);
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

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

}
