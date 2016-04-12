package com.app.empire.protocol.data.equip;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 精炼装备结果
 */
public class RefineEquipOk extends AbstractData {
	int heroId; // 英雄流水id
	int rank; // 军阶
	int equipNo; // 精炼的装备栏编号
	int proAdd; // 精炼属性加成
	int goodsStar; // 当前精炼星级
	int goodsExp; // 当前精炼经验

	public RefineEquipOk(int sessionId, int serial) {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_RefineEquipOk, sessionId, serial);
	}
	public RefineEquipOk() {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_RefineEquipOk);
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
	public int getProAdd() {
		return proAdd;
	}
	public void setProAdd(int proAdd) {
		this.proAdd = proAdd;
	}
	public int getGoodsStar() {
		return goodsStar;
	}
	public void setGoodsStar(int goodsStar) {
		this.goodsStar = goodsStar;
	}
	public int getGoodsExp() {
		return goodsExp;
	}
	public void setGoodsExp(int goodsExp) {
		this.goodsExp = goodsExp;
	}

}
