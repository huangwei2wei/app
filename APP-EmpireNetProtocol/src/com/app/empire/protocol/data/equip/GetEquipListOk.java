package com.app.empire.protocol.data.equip;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 英雄装备列表数据
 * 
 * @author doter
 */
public class GetEquipListOk extends AbstractData {
	private int[] heroId;// 英雄流水id
	private int[] rank; // 当前军衔阶段
	private int[] achieveProAdd; // 装备成就属性加成
	private String[] achieve; // 装备成就格式 1:3:6,2:3:6
	private int[] equipNo;// 装备栏编码
	private int[] equipId; // 装备id
	private int[] equipExp; // 物品精炼经验
	private int[] equipStar; // 物品星级
	private int[] equipQuality; // 物品品质
	private String[] equipPro; // 物品属性
	private int[] proAdd;// 精炼属性加成

	public GetEquipListOk(int sessionId, int serial) {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_GetEquipListOk, sessionId, serial);
	}

	public GetEquipListOk() {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_GetEquipListOk);
	}

	public int[] getHeroId() {
		return heroId;
	}

	public void setHeroId(int[] heroId) {
		this.heroId = heroId;
	}

	public int[] getRank() {
		return rank;
	}

	public void setRank(int[] rank) {
		this.rank = rank;
	}

	public int[] getAchieveProAdd() {
		return achieveProAdd;
	}

	public void setAchieveProAdd(int[] achieveProAdd) {
		this.achieveProAdd = achieveProAdd;
	}

	public String[] getAchieve() {
		return achieve;
	}

	public void setAchieve(String[] achieve) {
		this.achieve = achieve;
	}

	public int[] getEquipNo() {
		return equipNo;
	}

	public void setEquipNo(int[] equipNo) {
		this.equipNo = equipNo;
	}

	public int[] getEquipId() {
		return equipId;
	}

	public void setEquipId(int[] equipId) {
		this.equipId = equipId;
	}

	public int[] getEquipExp() {
		return equipExp;
	}

	public void setEquipExp(int[] equipExp) {
		this.equipExp = equipExp;
	}

	public int[] getEquipStar() {
		return equipStar;
	}

	public void setEquipStar(int[] equipStar) {
		this.equipStar = equipStar;
	}

	public int[] getEquipQuality() {
		return equipQuality;
	}

	public void setEquipQuality(int[] equipQuality) {
		this.equipQuality = equipQuality;
	}

	public String[] getEquipPro() {
		return equipPro;
	}

	public void setEquipPro(String[] equipPro) {
		this.equipPro = equipPro;
	}

	public int[] getProAdd() {
		return proAdd;
	}

	public void setProAdd(int[] proAdd) {
		this.proAdd = proAdd;
	}

}
