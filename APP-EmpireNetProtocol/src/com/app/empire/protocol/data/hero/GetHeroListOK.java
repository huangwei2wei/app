package com.app.empire.protocol.data.hero;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetHeroListOK extends AbstractData {
	private int[] heroId;// 英雄流水id
	private int[] heroExtId;// 英雄扩展id
	private int[] experience;// 经验
	private int[] lv;// 等级
	private int[] fighting;// 战斗力
	private String[] property;// 属性
	private int[] talent;// 天赋数
	private int[] useTalent;// 使用了多少天赋值

	public GetHeroListOK(int sessionId, int serial) {
		super(Protocol.MAIN_HERO, Protocol.HERO_GetHeroListOK, sessionId, serial);
	}
	public GetHeroListOK() {
		super(Protocol.MAIN_HERO, Protocol.HERO_GetHeroListOK);
	}
	public int[] getHeroId() {
		return heroId;
	}
	public void setHeroId(int[] heroId) {
		this.heroId = heroId;
	}
	public int[] getHeroExtId() {
		return heroExtId;
	}
	public void setHeroExtId(int[] heroExtId) {
		this.heroExtId = heroExtId;
	}
	public int[] getExperience() {
		return experience;
	}
	public void setExperience(int[] experience) {
		this.experience = experience;
	}
	public int[] getLv() {
		return lv;
	}
	public void setLv(int[] lv) {
		this.lv = lv;
	}
	public int[] getFighting() {
		return fighting;
	}
	public void setFighting(int[] fighting) {
		this.fighting = fighting;
	}
	public String[] getProperty() {
		return property;
	}
	public void setProperty(String[] property) {
		this.property = property;
	}
	public int[] getTalent() {
		return talent;
	}
	public void setTalent(int[] talent) {
		this.talent = talent;
	}
	public int[] getUseTalent() {
		return useTalent;
	}
	public void setUseTalent(int[] useTalent) {
		this.useTalent = useTalent;
	}

}
