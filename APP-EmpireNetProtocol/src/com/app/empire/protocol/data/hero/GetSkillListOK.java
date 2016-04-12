package com.app.empire.protocol.data.hero;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetSkillListOK extends AbstractData {
	
	private int[] heroId;// 英雄id
	private int[] skillBaseId;// 技能基表id
	private int[] skillExtId;// 技能扩展id
	private int[] lv;// 等级
	private String[] property;// 属性

	public GetSkillListOK(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_GetSkillListOK, sessionId, serial);
	}
	public GetSkillListOK() {
		super(Protocol.MAIN_HERO, Protocol.HERO_GetSkillListOK);
	}
	public int[] getHeroId() {
		return heroId;
	}
	public void setHeroId(int[] heroId) {
		this.heroId = heroId;
	}
	public int[] getSkillBaseId() {
		return skillBaseId;
	}
	public void setSkillBaseId(int[] skillBaseId) {
		this.skillBaseId = skillBaseId;
	}
	public int[] getSkillExtId() {
		return skillExtId;
	}
	public void setSkillExtId(int[] skillExtId) {
		this.skillExtId = skillExtId;
	}
	public int[] getLv() {
		return lv;
	}
	public void setLv(int[] lv) {
		this.lv = lv;
	}
	public String[] getProperty() {
		return property;
	}
	public void setProperty(String[] property) {
		this.property = property;
	}

}
