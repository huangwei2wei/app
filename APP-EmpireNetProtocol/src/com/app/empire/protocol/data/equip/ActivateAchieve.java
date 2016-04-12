package com.app.empire.protocol.data.equip;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 激活装备成就
 * 
 * @author doter
 */
public class ActivateAchieve extends AbstractData {

	private int heroId; // 英雄流水id
	private int achieveId;// 要激活的成就id

	public ActivateAchieve(int sessionId, int serial) {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_ActivateAchieve, sessionId, serial);
	}
	public ActivateAchieve() {
		super(Protocol.MAIN_EQUIP, Protocol.EQUIP_ActivateAchieve);
	}

	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}
	public int getAchieveId() {
		return achieveId;
	}
	public void setAchieveId(int achieveId) {
		this.achieveId = achieveId;
	}

}
