package com.app.empire.protocol.data.equip;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 穿戴装备
 * 
 * @author doter
 * 
 */
public class WearEquip extends AbstractData {
	int goodsId; // 物品流水id
	int heroId; // 英雄游戏流水id
	int rank; // 军阶
	int equipNo; // 穿戴装备栏编号

	public WearEquip(int sessionId, int serial) {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_WearEquip, sessionId, serial);
	}

	public WearEquip() {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_WearEquip);
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
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

}
