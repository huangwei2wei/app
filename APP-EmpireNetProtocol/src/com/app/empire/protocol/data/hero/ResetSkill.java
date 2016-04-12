package com.app.empire.protocol.data.hero;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 重置英雄技能
 * 
 * @author doter
 * 
 */

public class ResetSkill extends AbstractData {
	private int heroId;// 英雄流水id

	public ResetSkill(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_ResetSkill, sessionId, serial);
	}
	public ResetSkill() {
		super(Protocol.MAIN_HERO, Protocol.HERO_ResetSkill);
	}
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}

}
