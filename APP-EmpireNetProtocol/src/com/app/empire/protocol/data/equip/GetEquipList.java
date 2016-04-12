package com.app.empire.protocol.data.equip;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 获取英雄装备列表
 */
public class GetEquipList extends AbstractData {
	private int heroId[]; // 英雄流水id
	
	public GetEquipList(int sessionId, int serial) {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_GetEquipList, sessionId, serial);
	}
	public GetEquipList() {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_GetEquipList);
	}
	public int[] getHeroId() {
		return heroId;
	}
	public void setHeroId(int[] heroId) {
		this.heroId = heroId;
	}

}
