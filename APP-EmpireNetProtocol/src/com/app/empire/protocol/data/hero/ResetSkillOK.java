package com.app.empire.protocol.data.hero;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 重置英雄技能结果
 * 
 * @author doter
 * 
 */
public class ResetSkillOK extends AbstractData {
	private int heroId;// 英雄流水id
	private int talent;// 现有天赋值

	public ResetSkillOK(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_ResetSkillOK, sessionId, serial);
	}
	public ResetSkillOK() {
		super(Protocol.MAIN_HERO, Protocol.HERO_ResetSkillOK);
	}

	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}
	public int getTalent() {
		return talent;
	}
	public void setTalent(int talent) {
		this.talent = talent;
	}

}
