package com.app.empire.protocol.data.hero;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 技能学习升级
 * 
 * @author doter
 *
 */
public class StudySkill extends AbstractData {
	private int heroId;// 要学习升级的玩家英雄流水号id
	private int skillBaseId;// 要学习升级的技能基表id

	public StudySkill(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_StudySkill, sessionId, serial);
	}
	public StudySkill() {
		super(Protocol.MAIN_HERO, Protocol.HERO_StudySkill);
	}
	public int getHeroId() {
		return heroId;
	}
	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}
	public int getSkillBaseId() {
		return skillBaseId;
	}
	public void setSkillBaseId(int skillBaseId) {
		this.skillBaseId = skillBaseId;
	}

}
